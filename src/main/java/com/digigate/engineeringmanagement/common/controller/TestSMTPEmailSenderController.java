package com.digigate.engineeringmanagement.common.controller;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.SmtpMailSender;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class TestSMTPEmailSenderController {

    private final SmtpMailSender smtpMailSender;

    public TestSMTPEmailSenderController(SmtpMailSender smtpMailSender) {
        this.smtpMailSender = smtpMailSender;
    }


    @GetMapping("/send")
    public String sendMail() {

        try {
            smtpMailSender.sendMail("ehteshamul.tamvir@digigate360.com","Test Subject","Test Body");
            return "Successfully sent E-mail";
        }catch (Exception e){
            throw new EngineeringManagementServerException(
                    ErrorId.EMAIL_SENT_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }
}
