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

    public PrepareInfo random(){
        this.url = "url";
        this.method = "method";
        this.headers = Map.of("key", "value");
        this.body = Map.of("key", "value");
        return this;
    }
}
