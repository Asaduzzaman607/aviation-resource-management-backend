package com.digigate.engineeringmanagement.common.service;

import com.digigate.engineeringmanagement.common.payload.response.MailResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender sender;

    private final Configuration config;

    @Value("${mail.default.to}")
    private String[] defaultTos;

    @Autowired
    public EmailService(JavaMailSender sender,
                        Configuration config) {

        this.sender = sender;
        this.config = config;
    }

    public MailResponse sendEmail(Map<String, Object> model, String subject, String template, List<String> recipients) {

        MailResponse response = new MailResponse();
        MimeMessage message = sender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Template t = config.getTemplate(template);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setTo(ArrayUtils.addAll(recipients.toArray(String[]::new)));
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom("arm@usbair.com");
            sender.send(message);

            response.setMessage("mail send Successfully ");
            response.setStatus(Boolean.TRUE);

        } catch (MessagingException | IOException | TemplateException e) {
            response.setMessage("Mail Sending failure : " + e.getMessage());
            response.setStatus(Boolean.FALSE);
        }
        return response;
    }

}
