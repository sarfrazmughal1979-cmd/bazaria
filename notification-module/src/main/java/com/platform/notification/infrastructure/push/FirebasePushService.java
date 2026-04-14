package com.platform.notification.infrastructure.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushService {
    public void sendToDevice(String deviceToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(notification)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Push sent: {}", response);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
