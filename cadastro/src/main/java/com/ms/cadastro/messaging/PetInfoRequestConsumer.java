package com.ms.cadastro.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.cadastro.dto.PetEventDTO;
import com.ms.cadastro.dto.PetInfoRequestEvent;
import com.ms.cadastro.exception.PetNotFoundException;
import com.ms.cadastro.mapper.PetMapper;
import com.ms.cadastro.model.Pet;
import com.ms.cadastro.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PetInfoRequestConsumer {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final PetProducer petProducer;

    //Escuta eventos de pet_info_request publicados pelo serviço de Agendamento.
    //Escuta a fila pet.requests e responde com os dados do pet correspondente.
    //Este listener existe apenas no microsserviço de Cadastro
    //A resposta (pet_info_response) é publicada de volta na fila pet.responses.
    @RabbitListener(queues = "${broker.queue.pet_request}")
    public void receivePetInfoRequest(PetInfoRequestEvent request) {
        System.out.println("[RABBITMQ] Requisição recebida: petId = " + request.getPetId());

        Pet pet = petRepository.findByIdWithOwners(request.getPetId())
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + request.getPetId()));

        PetEventDTO response = petMapper.toPetEventDTO(pet);

        petProducer.sendPetInfoResponse(response);
    }
}
