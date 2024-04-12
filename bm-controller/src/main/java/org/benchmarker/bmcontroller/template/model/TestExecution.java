package org.benchmarker.bmcontroller.template.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmcontroller.common.model.BaseTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_execution")
@ToString
public class TestExecution extends BaseTime {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
    private TestTemplate testTemplate;

    @OneToMany(mappedBy = "testExecution", fetch = FetchType.LAZY)
    private List<TestResult> testResults = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AgentStatus agentStatus;

}