package com.ms.agendamento.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.agendamento.dto.PetEventDTO;
import com.ms.agendamento.service.AppointmentService;

import lombok.RequiredArgsConstructor;

//Consumer que escuta eventos pet_created e aciona criação automática de agendamentos.
//Escuta o evento pet_created, publicado pelo microsserviço de Cadastro, 
//e cria agendamentos automáticos com base em regras clínicas (idade, espécie, etc).
@Component
@RequiredArgsConstructor
public class PetCreatedConsumer {
    private final AppointmentService appointmentService;

    @RabbitListener(queues = "${broker.queue.pet_created}")
    public void receive(PetEventDTO event) {
        System.out.println("[PET CREATED] Received: " + event);
        // Cria agendamentos automáticos com base no novo pet
        appointmentService.handleAutomaticAppointments(event);
    }
    @RabbitListener(queues = "${broker.queue.pet_updated}")
    public void receiveUpdate(PetEventDTO event) {
        System.out.println("[PET UPDATED] Received: " + event);
        appointmentService.updateAppointmentsIfNecessary(event);
    }
}
