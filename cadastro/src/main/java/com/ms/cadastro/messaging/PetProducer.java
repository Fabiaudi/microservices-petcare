package com.ms.cadastro.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.cadastro.dto.PetEventDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PetProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.routingkey.pet_created}")
    private String createdRoutingKey;

    @Value("${broker.routingkey.pet_updated}")
    private String updatedRoutingKey;

    @Value("${broker.routingkey.pet_info_response}")
    private String infoResponseRoutingKey;

    // Envia evento de criação
    public void sendCreated(PetEventDTO event) {
        System.out.println("[RABBITMQ] Enviando pet_created → " + exchange);
        rabbitTemplate.convertAndSend(exchange, createdRoutingKey, event);
    }

    // Envia evento de atualização
    public void sendUpdated(PetEventDTO event) {
        System.out.println("[RABBITMQ] Enviando pet_updated → " + exchange);
        rabbitTemplate.convertAndSend(exchange, updatedRoutingKey, event);
    }

    // Responde ao pedido de informações do pet
    public void sendPetInfoResponse(PetEventDTO event) {
        System.out.println("[RABBITMQ] Respondendo pet_info_response → " + exchange);
        rabbitTemplate.convertAndSend(exchange, infoResponseRoutingKey, event);
    }
}
