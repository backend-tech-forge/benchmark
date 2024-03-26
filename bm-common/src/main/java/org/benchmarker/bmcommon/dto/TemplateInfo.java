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

    private String id;
    private String name;
    private String description;
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private PrepareInfo prepare;

    public TemplateInfo random(){
        this.id = "id";
        this.name = "name";
        this.description = "description";
        this.url = "url";
        this.method = "method";
        this.headers = Map.of("key", "value");
        this.body = Map.of("key", "value");
        this.prepare = new PrepareInfo().random();
        return this;
    }
}
