package org.benchmarker.bmcontroller.common.model;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcontroller.common.util.DateUtil;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public class BaseTime {

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime updatedAt;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = DateUtil.getCurrentTime();
        }
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = DateUtil.getCurrentTime();
    }
    
    @Override
    public String toString() {
        return "BaseTime{" +
            "createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }

}