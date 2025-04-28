package com.ms.agendamento.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.agendamento.dto.AppointmentResponseDTO;
import com.ms.agendamento.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentConsumer {

    private final AppointmentService appointmentService;

    @RabbitListener(queues = "${broker.queue.appointment_confirmed_bymail}")
    public void handleAppointmentConfirmed(AppointmentResponseDTO dto) {
        System.out.println("[CONFIRMADO] Recebido confirmação via e-mail para ID: " + dto.getId());
        appointmentService.confirm(dto.getId());
    }

    @RabbitListener(queues = "${broker.queue.appointment_cancelled_bymail}")
    public void handleAppointmentCancelled(AppointmentResponseDTO dto) {
        System.out.println("[CANCELAMENTO] Pedido de cancelamento via e-mail para ID: " + dto.getId());
        boolean deleted = appointmentService.deleteIfExists(dto.getId());
        if (deleted) {
            System.out.println("[CANCELADO] Agendamento ID " + dto.getId() + " foi deletado com sucesso.");
        } else {
            System.out.println("[IGNORADO] Agendamento ID " + dto.getId() + " já estava deletado.");
        }
    }
}
