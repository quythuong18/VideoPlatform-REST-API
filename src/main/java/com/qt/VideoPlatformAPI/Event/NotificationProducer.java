package com.qt.VideoPlatformAPI.Event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class NotificationProducer {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String notificationRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMsg(NotificationEvent eventMessage) {
        rabbitTemplate.convertAndSend(exchangeName, notificationRoutingKey, eventMessage);
        System.out.println(eventMessage.toString());
    }
}
