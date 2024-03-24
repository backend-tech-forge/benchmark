package org.benchmarker.bmcommon.dto;


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

    private Integer id;
    private String name;
    private String description;
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private PrepareInfo prepare;

    public TemplateInfo random(){
        this.id = 1;
        this.name = "테스트";
        this.description = "description";
        this.url = "http://localhost:8080/";
        this.method = "get";
        this.headers = Map.of("key", "value");
        this.body = Map.of("key", "value");
        this.prepare = new PrepareInfo().random();
        return this;
    }
}
