package com.ms.notificacao.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.notificacao.dto.AppointmentResponseDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentConfirmationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.routingkey.appointment_confirmed}")
    private String appointmentConfirmedRoutingKey;

    @Value("${broker.routingkey.appointment_cancelled_request}")
    private String appointmentCancelledRequestRoutingKey;

    public void sendConfirmation(AppointmentResponseDTO dto) {
        System.out.println("[RABBITMQ] Enviando appointment.confirmed → " + exchange);
        rabbitTemplate.convertAndSend(exchange, appointmentConfirmedRoutingKey, dto);
    }

    public void sendCancellation(AppointmentResponseDTO dto) {
        System.out.println("[RABBITMQ] Enviando appointment.cancel.request → " + exchange);
        rabbitTemplate.convertAndSend(exchange, appointmentCancelledRequestRoutingKey, dto);
    }
}
