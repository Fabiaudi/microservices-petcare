package com.ms.agendamento.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.agendamento.dto.PetEventDTO;
import com.ms.agendamento.service.AppointmentService;

import lombok.RequiredArgsConstructor;

//Consumer que escuta eventos pet_info_response e finaliza criação de agendamento manual.
//Escuta o evento pet_info_response quando o serviço de Cadastro responde com os dados do pet solicitados.
//Usado para completar o agendamento manual iniciado anteriormente.
@Component
@RequiredArgsConstructor
public class PetInfoResponseConsumer {
    private final AppointmentService appointmentService;

    @RabbitListener(queues = "${broker.queue.pet_responses}")
    public void receive(PetEventDTO event) {
        System.out.println("[PET INFO RESPONSE] Received: " + event);
        // Cria agendamento manual pendente, usando os dados recebidos do pet
        appointmentService.handlePetInfoResponse(event);
    }
}
