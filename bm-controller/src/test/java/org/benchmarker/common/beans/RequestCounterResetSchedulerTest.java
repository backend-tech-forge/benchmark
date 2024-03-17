package org.benchmarker.common.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest
class RequestCounterResetSchedulerTest {

    @SpyBean
    private RequestCounter requestCounter;

    @Test
    @DisplayName("5초 마다 스케줄러가 정확히 동작하는지 확인하기")
    public void testResetRequestCounter() throws InterruptedException {
        // given
        ThreadPoolTaskScheduler taskSchedulerMock = mock(ThreadPoolTaskScheduler.class);
        CountDownLatch latch = new CountDownLatch(1);

        // when
        requestCounter.reset();
        verify(requestCounter, atLeastOnce()).reset();

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(taskSchedulerMock).schedule(any(Runnable.class), any(CronTrigger.class));

        // 테스트 대상 객체 생성
        RequestCounterResetScheduler scheduler = new RequestCounterResetScheduler(requestCounter);
        latch.await(10, TimeUnit.SECONDS);

        // then
        verify(requestCounter, atLeastOnce()).reset();
    }

}