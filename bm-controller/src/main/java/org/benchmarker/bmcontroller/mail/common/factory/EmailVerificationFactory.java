package org.benchmarker.bmcontroller.mail.common.factory;

public class EmailVerificationFactory implements EmailBodyGenerator {

    @Override
    public String createBody(String... args) {

        String authNum = args[0];

        return "안녕하세요!\n\n, 회원 가입을 위한 인증 코드를 안내드립니다. 아래의 인증 코드를 입력하여 계정을 활성화하세요:\n\n인증 코드: " + authNum;
    }
}
