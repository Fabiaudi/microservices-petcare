package com.ms.agendamento.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.agendamento.dto.AppointmentResponseDTO;

import lombok.RequiredArgsConstructor;

//Produtor responsável por publicar eventos de agendamentos criados.
//Publica o evento appointment.created após salvar um agendamento
//esse evento será consumido pelo microsserviço de Notificações.
@Component
@RequiredArgsConstructor
public class AppointmentProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.routingkey.appointment_created}")
    private String createdRoutingKey;

    @Value("${broker.routingkey.appointment_updated}")
    private String updatedRoutingKey;

    @Value("${broker.routingkey.appointment_cancelled_send}")
    private String cancelledRoutingKey;

    public void publish(AppointmentResponseDTO appointment) {
        rabbitTemplate.convertAndSend(exchange, createdRoutingKey, appointment);
        System.out.println("[RABBITMQ] appointment.created → " + exchange);
    }

    public void publishUpdated(AppointmentResponseDTO appointment) {
        rabbitTemplate.convertAndSend(exchange, updatedRoutingKey, appointment);
        System.out.println("[RABBITMQ] appointment.updated → " + exchange);
    }

    public void publishCancelled(AppointmentResponseDTO appointment) {
        rabbitTemplate.convertAndSend(exchange, cancelledRoutingKey, appointment);
        System.out.println("[RABBITMQ] send.appointment.cancelled → " + exchange);
    }
}
