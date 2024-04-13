package org.benchmarker.bmcontroller.mail.common.factory;

public interface EmailBodyGenerator {

    String createBody(String... args);
}
