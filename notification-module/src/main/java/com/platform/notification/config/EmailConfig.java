<<<<<<<< HEAD:notification-module/src/main/java/com/platform/notification/config/EmailConfig.java
package com.platform.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("://gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("sarfraz.mughal1979@gmail.com");
        mailSender.setPassword("Mughal12");
        mailSender.getJavaMailProperties().put("mail.transport.protocol", "smtp");;
        mailSender.getJavaMailProperties().put("mail.smtp.auth", "true");
        mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
        // ... set other properties
        return mailSender;
    }

}
========
>>>>>>>> origin/master:notification-module/src/main/java/com/platform/notification/EmailConfig.java
