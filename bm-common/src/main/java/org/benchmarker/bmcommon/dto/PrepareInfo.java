package org.benchmarker.bmcommon.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrepareInfo {

    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, Object> body;
}
