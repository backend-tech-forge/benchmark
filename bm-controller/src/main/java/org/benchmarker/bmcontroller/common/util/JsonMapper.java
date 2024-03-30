package org.benchmarker.bmcontroller.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    /**
     * Convert json string to map
     *
     * @param json string
     * @return Map key: string value: object
     * @throws Exception
     */
    public Map<String, Object> jsonStringToMap(String json) throws Exception {
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    public Map<String, String> jsonStringToMapString(String json) throws Exception {
        return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
    }

    public String mapToJsonString(Map<String, Object> map) throws Exception {
        return objectMapper.writeValueAsString(map);
    }

    /**
     * Check if the given string is a valid json
     * @param jsonString
     * @return boolean
     */
    public boolean isValidJson(String jsonString) {
        try {
            this.jsonStringToMap(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
