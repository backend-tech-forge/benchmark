package org.benchmarker.bmcontroller.security.util;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
@ToString
public class MethodUrlPair {
    private final List<String> method;
    private final String url;
}
