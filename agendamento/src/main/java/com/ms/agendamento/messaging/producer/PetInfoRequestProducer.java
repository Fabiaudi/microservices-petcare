package com.ms.agendamento.messaging.producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.agendamento.dto.PetInfoRequestEventDTO;

import lombok.RequiredArgsConstructor;


//Produtor responsável por enviar requisições de dados de pet para o serviço de Cadastro.
//Publica o evento pet_info_request (na fila pet.requests) com o petId, 
//quando é feito um agendamento manual e os dados do pet ainda não existem no serviço.
@Component
@RequiredArgsConstructor
public class PetInfoRequestProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.name}")
    private String exchange;

    @Value("${broker.routingkey.pet_info_request}")
    private String routingKey;

    public void sendRequest(PetInfoRequestEventDTO event) {
        // Envia solicitação para o serviço de cadastro via RabbitMQ
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        System.out.println("[PET INFO REQUEST] Sent: " + event);
    }
}