package com.platform.notification.infrastructure.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Content;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesEmailService {
    private final SesClient sesClient;

    public void sendEmail(String to, String subject, String body) {
        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(to).build())
                .message(software.amazon.awssdk.services.ses.model.Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(software.amazon.awssdk.services.ses.model.Body.builder()
                                .html(Content.builder().data(body).build())
                                .build())
                        .build())
                .build();
        sesClient.sendEmail(request);
        log.info("Email sent to {}", to);
    }
}
