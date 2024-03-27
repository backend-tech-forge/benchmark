package org.benchmarker.bmcommon.dto;


import java.time.Duration;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TemplateInfo {

    private String id;
    private String name;
    private String description;
    private String url;
    private String method;
    private Integer vuser;
    private Integer maxRequest;
    private Duration maxDuration;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private String prepareScript;

}
