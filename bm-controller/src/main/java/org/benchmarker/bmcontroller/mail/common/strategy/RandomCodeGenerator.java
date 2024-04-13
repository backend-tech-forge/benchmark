package org.benchmarker.bmcontroller.mail.common.strategy;

import java.util.Random;

public class RandomCodeGenerator implements IRandomCodeGenerator {

    private static final int CODE_LENGTH = 6;

    @Override
    public String generateVerificationCode() {

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            codeBuilder.append(random.nextInt(10));
        }

        return codeBuilder.toString();
    }
}
