package com.platform.core.cloud.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class FcmPushService {

    @Value("${fcm.service-account-file}")
    private String serviceAccountFile;

    @PostConstruct
    public void initialize() {
        try {
            var options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(serviceAccountFile).getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase", e);
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    public void sendToUser(String deviceToken, String title, String body) {
        sendToUser(deviceToken, title, body, null);
    }

    public void sendToUser(String deviceToken, String title, String body, Map<String, String> data) {
        try {
            var notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            var messageBuilder = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(notification);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM notification to token: {}", deviceToken, e);
            throw new RuntimeException("FCM notification failed", e);
        }
    }
}