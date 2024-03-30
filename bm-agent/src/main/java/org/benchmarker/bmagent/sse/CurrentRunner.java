package org.benchmarker.bmagent.sse;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.benchmarker.bmagent.pref.HttpSender;
import org.benchmarker.bmcommon.dto.TemplateInfo;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentRunner {
    private HttpSender httpSender;
    private LocalDateTime startAt;
    private String groupId;
    private TemplateInfo templateInfo;
}
