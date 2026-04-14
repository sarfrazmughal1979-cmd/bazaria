package com.platform.notification.application.channel;

import com.platform.notification.domain.model.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class NotificationChannelFactory {

    private final Map<NotificationChannel, NotificationChannelAdapter> adapters;

    public NotificationChannelFactory(
            EmailChannel emailChannel,
            SmsChannel smsChannel,
            PushNotificationChannel pushChannel,
            InAppChannel inAppChannel) {
        adapters = new EnumMap<>(NotificationChannel.class);
        adapters.put(NotificationChannel.EMAIL, emailChannel);
        adapters.put(NotificationChannel.SMS, smsChannel);
        adapters.put(NotificationChannel.PUSH, pushChannel);
        adapters.put(NotificationChannel.IN_APP, inAppChannel);
    }

    public NotificationChannelAdapter getAdapter(NotificationChannel channel) {
        NotificationChannelAdapter adapter = adapters.get(channel);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for channel: " + channel);
        }
        return adapter;
    }
}