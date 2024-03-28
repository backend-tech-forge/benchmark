package org.benchmarker.bmcontroller.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JsonMapperTest {
    private JsonMapper jsonMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.objectMapper = objectMapper;
        jsonMapper = new JsonMapper(this.objectMapper);
    }

    @Test
    @DisplayName("JsonMapper jsonStringToMapString Test")
    public void test() throws Exception {
        // given
        Map<String, String> str = jsonMapper.jsonStringToMapString(
            "{ \"key\": \"value\" }");
        String s = jsonMapper.mapToJsonString(Map.of("key", "value"));

        // when & then
        assertThat(str).isEqualTo(Map.of("key", "value"));
        assertThat(s).isEqualTo("{\"key\":\"value\"}");
        assertThat(jsonMapper.isValidJson("{ \"key\": \"value\" }")).isTrue();
        assertThat(jsonMapper.isValidJson("aa")).isFalse();
        assertThat(jsonMapper.isValidJson("1")).isFalse();
    }
}