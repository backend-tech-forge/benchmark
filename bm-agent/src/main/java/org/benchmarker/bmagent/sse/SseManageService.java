package org.benchmarker.bmagent.sse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmagent.consts.PreftestConsts;
import org.benchmarker.bmagent.pref.HttpSender;
import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.service.AbstractSseManageService;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseManageService for managing SseEmitter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SseManageService extends AbstractSseManageService {

    private final IScheduledTaskService scheduledTaskService;
    private final ResultManagerService resultManagerService;
    private final AgentStatusManager agentStatusManager;
    private HashMap<Long, CurrentRunner> runner = new HashMap<>();

    /**
     * Start the SseEmitter for the given id and return the SseEmitter
     *
     * <p>Emits TestResult to id in every 1 second by running {@link ScheduledTaskService#start}
     *
     * @param id Long
     * @return SseEmitter
     * @see ScheduledTaskService
     */
    @Override
    public SseEmitter start(Long id, String groupId, TemplateInfo templateInfo) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        LocalDateTime startAt = LocalDateTime.now();
        // when the client disconnects, complete the SseEmitter
        alwaysDoStop(id, emitter);

        if (sseEmitterHashMap.get(id) != null) {
            log.debug("SSE already exists for template ID: {}", id);
            return null;
        }

        // Save the SseEmitter to the map
        LocalDateTime startTime = LocalDateTime.now();
        sseEmitterHashMap.put(id, emitter);

        HttpSender httpSender = new HttpSender(resultManagerService, scheduledTaskService,
            agentStatusManager);

        runner.put(id,
            CurrentRunner.builder()
                .httpSender(httpSender)
                .groupId(groupId)
                .startAt(startTime)
                .templateInfo(templateInfo).build());

        // 1초마다 TestResult 를 보내는 스케줄러 시작
        scheduledTaskService.start(id, () -> {
            LocalDateTime curTime = LocalDateTime.now();
            CommonTestResult data = getCommonTestResult(groupId, templateInfo, httpSender,
                startTime, curTime);
            data.setFinishedAt("-");

            resultManagerService.save(id, data);
            send(id, resultManagerService.find(id));
        }, 0, 1, TimeUnit.SECONDS);

        // async + non-blocking 필수
        CompletableFuture.runAsync(() -> {
            try {
                httpSender.sendRequests(emitter, templateInfo);
                LocalDateTime finished = LocalDateTime.now();
                CommonTestResult data = getCommonTestResult(groupId, templateInfo, httpSender,
                    startTime,
                    finished);
                data.setFinishedAt(finished.toString());
                if (httpSender.getIsErrorExceed()) { // error rate exceeded
                    data.setTestStatus(AgentStatus.STOP_BY_ERROR);
                } else if (!httpSender.getIsRunning()) { // bm-controller send stop sign
                    data.setTestStatus(AgentStatus.STOP);
                } else { // performance test finished
                    data.setTestStatus(AgentStatus.TESTING_FINISH);
                }
                send(id, data);
                emitter.complete();
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }
        });

        return emitter;
    }

    /**
     * Get information from {@link HttpSender}, {@link AgentStatusManager}, and convert to
     * {@link CommonTestResult}
     *
     * @param templateInfo
     * @param htps
     * @param start
     * @param cur
     * @return CommonTestResult
     */
    private CommonTestResult getCommonTestResult(String groupId, TemplateInfo templateInfo,
        HttpSender htps, LocalDateTime start, LocalDateTime cur) {
        List<Double> percentiles = PreftestConsts.percentiles;
        return CommonTestResult.builder()
            .groupId(groupId)
            .startedAt(start.toString())
            .totalRequests(htps.getTotalRequests().get())
            .totalSuccess(htps.getTotalSuccess().get())
            .totalErrors(htps.getTotalErrors().get())
            .statusCodeCount(htps.getStatusCodeCount())
            .testId(Integer.parseInt(templateInfo.getId()))
            .url(templateInfo.getUrl())
            .method(templateInfo.getMethod())
            .totalUsers(templateInfo.getVuser())
            .totalDuration(Duration.between(start, cur).toString())
            .MTTFBPercentiles(htps.calculateMttfbPercentile(percentiles))
            .TPSPercentiles(htps.calculateTpsPercentile(percentiles))
            .testStatus(agentStatusManager.getStatus().get())
            .finishedAt(cur.toString())
            .mttfbAverage(htps.getMttfbAvg().toString())
            .tpsAverage(htps.getTpsAvg())
            .build();
    }

    /**
     * Stop the SseEmitter for the given id
     *
     * <p>Shutdown the {@link ScheduledTaskService} and remove the SseEmitter from the map
     *
     * @param id Long
     */
    @Override
    public void stop(Long id) {
        SseEmitter emitter = sseEmitterHashMap.remove(id);
        if (emitter != null) {
            log.info("remove sse emitter");
            this.send(id, "SSE completed");
            emitter.complete();
        }

        runner.get(id).getHttpSender().cancelRequests();
        scheduledTaskService.shutdown(id); // but shutdown
        resultManagerService.remove(id);
        agentStatusManager.updateAgentStatus(AgentStatus.READY);
    }

    @Override
    public void stopSign(Long id) throws IOException {
        SseEmitter emitter = sseEmitterHashMap.remove(id);
        CurrentRunner curRunner = runner.get(id);
        CommonTestResult commonTestResult = this.getCommonTestResult(curRunner.getGroupId(),
            curRunner.getTemplateInfo(), curRunner.getHttpSender(),
            curRunner.getStartAt(), LocalDateTime.now());
        commonTestResult.setTestStatus(AgentStatus.STOP);

        if (emitter != null) {
            emitter.send(commonTestResult);
            log.info("remove sse emitter");
            this.send(id, "SSE completed");
            emitter.complete();
        }

        curRunner.getHttpSender().cancelRequests();
        runner.remove(id);
        scheduledTaskService.shutdown(id); // but shutdown
        resultManagerService.remove(id);
        agentStatusManager.updateAgentStatus(AgentStatus.READY);
    }

    @Override
    public void send(Long id, Object data) {
        try {
            SseEmitter emitter = sseEmitterHashMap.get(id);
            if (emitter != null) {
                emitter.send(data);
            }
        } catch (IOException e) {
            log.warn("IOException");
        }
    }

    /**
     * Always do stop for the given id and emitter
     *
     * @param id      Long
     * @param emitter SseEmitter
     */
    private void alwaysDoStop(Long id, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            log.info("SSE Completed");
            agentStatusManager.updateAgentStatus(AgentStatus.READY);
            this.stop(id);
        });
        emitter.onTimeout(() -> {
            log.warn("SSE Timeout");
            agentStatusManager.updateAgentStatus(AgentStatus.READY);
            this.stop(id);
        });
        emitter.onError((ex) -> {
            log.info("SSE connection error for template ID: {}", id);
            agentStatusManager.updateAgentStatus(AgentStatus.READY);
            this.stop(id); // Call method to clean up resources
        });
    }

}