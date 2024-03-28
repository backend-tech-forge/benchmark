package org.benchmarker.bmagent.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class AgentStatusManagerTest {

    @Test
    @DisplayName("Mutex lock 퍼포먼스 테스트 시작 전 설정")
    void test1() throws InterruptedException {
        // given
        AgentStatusManager agentStatusManager = new AgentStatusManager();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<CompletableFuture<AgentStatus>> cfs = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(); // TESTING 개수

        for (int i=0;i<10;i++){
            CompletableFuture<AgentStatus> cf = CompletableFuture.supplyAsync(() -> {
                Optional<AgentStatus> s = agentStatusManager.getAndUpdateStatusIfReady(
                    AgentStatus.TESTING);
                return s.orElse(AgentStatus.UNKNOWN);
            }, executorService);
            cfs.add(cf);
        }

        for (CompletableFuture<AgentStatus> cf : cfs) {
            // when
            cf.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    if (result.equals(AgentStatus.TESTING)){
                        count.getAndIncrement();
                    }
                } else {
                    throwable.printStackTrace();
                }
            });
        }

        // then
        assertThat(count.get()).isEqualTo(1);

    }

}