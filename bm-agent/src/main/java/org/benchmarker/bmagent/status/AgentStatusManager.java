package org.benchmarker.bmagent.status;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.springframework.stereotype.Component;

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
    // server start time
    private final ZonedDateTime startedAt = ZonedDateTime.now();

    // Mutex lock object
    private Object lock = new Object();

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
    public Optional<AgentStatus> getAndUpdateStatusIfReady(AgentStatus status){
        synchronized (lock){
            if (this.status.get()==AgentStatus.READY){
                this.status.set(status);
                return Optional.of(status);
            }else{
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
