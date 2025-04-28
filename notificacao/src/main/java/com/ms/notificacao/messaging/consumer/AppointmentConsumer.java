package com.ms.notificacao.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.notificacao.dto.AppointmentResponseDTO;
import com.ms.notificacao.service.AppointmentConfirmationService;
import com.ms.notificacao.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentConsumer {

    private final EmailService emailService;
    private final AppointmentConfirmationService confirmationService;

    @RabbitListener(queues = "${broker.queue.appointment_created}")
    public void handleAppointmentCreated(AppointmentResponseDTO dto) {
        System.out.println("[APPOINTMENT CREATED] Recebido: " + dto.getId());
        confirmationService.storeWithToken(dto);
        emailService.sendAppointmentConfirmation(dto);
    }

    @RabbitListener(queues = "${broker.queue.appointment_updated}")
    public void handleAppointmentUpdated(AppointmentResponseDTO dto) {
        System.out.println("[APPOINTMENT UPDATED] Recebido: " + dto.getId());
        emailService.sendAppointmentUpdate(dto);
    }

    @RabbitListener(queues = "${broker.queue.appointment_cancelled}")
    public void handleAppointmentCancelled(AppointmentResponseDTO dto) {
        System.out.println("[APPOINTMENT CANCELLED] Recebido: " + dto.getId());
        emailService.sendAppointmentCancellation(dto);
    }
}
