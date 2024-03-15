package org.benchmarker.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JsonMapperTest {
    private JsonMapper jsonMapper;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        jsonMapper = new JsonMapper(objectMapper);
    }

    @Test
    public void testJsonStringToMap() throws Exception {
        // given
        String json = "{ \"key\": \"value\" }";
        Map<String, Object> expectedMap = Map.of("key", "value");

        // when
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(expectedMap);
        Map<String, Object> resultMap = jsonMapper.jsonStringToMap(json);

        // then
        assertThat(resultMap).isEqualTo(expectedMap);
    }
}