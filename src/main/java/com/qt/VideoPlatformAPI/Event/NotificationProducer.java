package com.qt.VideoPlatformAPI.Event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;

    public void notificationProducer(NotificationEvent eventMessage) {
        rabbitTemplate.convertAndSend(eventMessage);
    }
}
