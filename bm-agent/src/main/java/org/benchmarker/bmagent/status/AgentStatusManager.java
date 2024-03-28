package org.benchmarker.bmagent.status;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentInfo;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmagent.consts.HeaderConst;
import org.benchmarker.bmagent.consts.SystemSchedulerConst;
import org.benchmarker.bmagent.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Agent instance status and information manager
 */
@Component
@Getter
@Setter
@ToString
@Slf4j
public class AgentStatusManager {

    private AtomicReference<AgentStatus> status = new AtomicReference<>(AgentStatus.READY);
    private double cpuUsage;
    private double memoryUsage;
    private Boolean isConnected = false;
    @Value("${controller.url}")
    private String controllerUrl;
    private final ZonedDateTime startedAt = ZonedDateTime.now();
    @Autowired
    private ServerProperties serverProperties;

    // Mutex lock object
    private Object lock = new Object();

    private AgentInfo getInfo() throws UnknownHostException {
        log.info(getServerProperties().toString());

        String agentServerUrl = "http://" + serverProperties.getAddress() + ":" + serverProperties.getPort();
        AgentInfo info = AgentInfo.builder()
            .cpuUsage(cpuUsage)
            .memoryUsage(memoryUsage)
            .startedAt(startedAt)
            .serverUrl(agentServerUrl)
            .status(status.get())
            .build();
        log.info(info.toString());

        return info;
    }

    // deprecate
    public boolean connect() {
        try {
            log.info("connection request to " + controllerUrl);
            HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(SystemSchedulerConst.connectControllerTimeout));

            ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            WebClient webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(controllerUrl) // 기본 URL 설정
                .build();

            String accessToken = JwtTokenProvider.createAccessToken();

            ClientResponse response = webClient.post()
                .header(HeaderConst.tokenKey, HeaderConst.bearerPrefix + accessToken)
                .bodyValue(getInfo())
                .exchange()
                .block();

            if (response != null && response.statusCode() == HttpStatus.OK) {
                // Response status is OK
                isConnected = true;
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Check the current status is READY state
     *
     * @return true if its READY, or return false
     */
    public boolean isReady() {
        return status.get() == AgentStatus.READY;
    }

    /**
     * Update status with mutex lock
     *
     * @param status
     */
    public void updateAgentStatus(AgentStatus status) {
        this.status.set(status);
    }

    /**
     * 동시접근 제어
     *
     * @param status
     * @return Optional empty if not READY, changed status if READY
     */
    public Optional<AgentStatus> getAndUpdateStatusIfReady(AgentStatus status) {
        synchronized (lock) {
            if (this.status.get() == AgentStatus.READY) {
                this.status.set(status);
                return Optional.of(status);
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Update cpuUsage, memoryUsage
     */
    public void updateStats() {
        // Update CPU usage
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        cpuUsage = osBean.getSystemCpuLoad();

        // Update memory usage
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        memoryUsage = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();
    }
}
