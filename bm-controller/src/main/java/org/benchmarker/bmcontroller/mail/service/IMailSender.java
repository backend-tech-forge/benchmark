package org.benchmarker.bmcontroller.mail.service;

import org.benchmarker.bmcontroller.mail.common.factory.EmailBodyGenerator;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailCertificationDto;
import org.benchmarker.bmcontroller.mail.controller.dto.EmailResDto;

public interface IMailSender {

    EmailResDto sendMail(EmailCertificationDto emailCertificationDto, EmailBodyGenerator emailBodyGenerator);
}
