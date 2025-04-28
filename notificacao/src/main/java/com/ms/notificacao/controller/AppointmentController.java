package com.ms.notificacao.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ms.notificacao.dto.AppointmentResponseDTO;
import com.ms.notificacao.messaging.producer.AppointmentConfirmationProducer;
import com.ms.notificacao.service.AppointmentConfirmationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/email/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Confirmation", description = "Confirmation and cancellation via email link")
public class AppointmentController {

    private final AppointmentConfirmationService confirmationService;
    private final AppointmentConfirmationProducer confirmationProducer;

    @GetMapping("/confirm")
    @Operation(summary = "Confirms an appointment via token and notifies the Appointment Service")
    public ResponseEntity<String> confirmAppointment(@RequestParam Long id, @RequestParam String token) {
        Optional<AppointmentResponseDTO> optional = confirmationService.validateToken(id, token);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Token inválido ou agendamento não encontrado.");
        }

        AppointmentResponseDTO confirmed = optional.get();
        confirmed.setConfirmed(true); // Essencial para controle no serviço de email
        confirmationProducer.sendConfirmation(confirmed);

        System.out.println("[CONFIRMAÇÃO] Agendamento ID " + id + " confirmado → evento enviado via RabbitMQ.");

        return ResponseEntity.ok("✅ Agendamento confirmado com sucesso!");
    }

    @GetMapping("/cancel")
    @Operation(summary = "Cancels an appointment via token and notifies the Appointment Service")
    public ResponseEntity<String> cancelAppointment(@RequestParam Long id, @RequestParam String token) {
        Optional<AppointmentResponseDTO> optional = confirmationService.validateToken(id, token);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Token inválido ou agendamento não encontrado.");
        }

        AppointmentResponseDTO cancelled = optional.get();
        cancelled.setConfirmed(true); // Identifica que o cancelamento veio do link
        confirmationProducer.sendCancellation(cancelled);

        System.out.println("[CANCELAMENTO] Agendamento ID " + id + " cancelado → evento enviado via RabbitMQ.");

        return ResponseEntity.ok("✅ Cancelamento solicitado com sucesso!");
    }
}
