package org.benchmarker.security.constant;

import java.util.Arrays;
import java.util.List;
import org.benchmarker.security.util.MethodUrlPair;

public interface URLConsts {

    List<MethodUrlPair> WHITE_LIST_URLS = List.of(
        new MethodUrlPair(Arrays.asList("POST","GET"), "/login"),
        new MethodUrlPair(Arrays.asList("POST"), "/user")
    );

}
