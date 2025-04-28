package com.ms.notificacao.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.notificacao.dto.PetEventDTO;
import com.ms.notificacao.service.EmailService;

import lombok.RequiredArgsConstructor;


//Consumer que escuta eventos pet.created e envia e-mail de boas-vindas para todos os tutores do pet.

@Component
@RequiredArgsConstructor
public class PetCreatedConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${broker.queue.pet_created}")
    public void receivePetCreated(PetEventDTO pet) {
        System.out.println("[PET CREATED] Recebido: " + pet.getName() + " - Enviando e-mails de boas-vindas...");
        emailService.sendWelcomeEmails(pet);
    }
}
