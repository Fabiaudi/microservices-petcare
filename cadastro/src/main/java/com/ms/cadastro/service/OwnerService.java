package com.ms.cadastro.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ms.cadastro.dto.OwnerDTO;
import com.ms.cadastro.exception.OwnerNotFoundException;
import com.ms.cadastro.mapper.PetMapper;
import com.ms.cadastro.model.Owner;
import com.ms.cadastro.model.Pet;
import com.ms.cadastro.repository.OwnerRepository;
import com.ms.cadastro.repository.PetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

    // Repositórios e mapper que o serviço utiliza
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    // // Retorna OwnerDTO (formato seguro e serializável para REST)
    public Optional<OwnerDTO> findByCpf(String cpf) {
        return ownerRepository.findByCpf(cpf)
                .map(petMapper::toOwnerDTO); // transforma a entidade em DTO
    }

    // Retorna todos os tutores (OwnerDTO) cadastrados no sistema
    public List<OwnerDTO> findAll() {
        return ownerRepository.findAll().stream()
                .map(petMapper::toOwnerDTO).collect(Collectors.toList());
    }

    // Retorna a entidade Owner (usado internamente para atualizar ou deletar)
    public Optional<Owner> findEntityByCpf(String cpf) {
        return ownerRepository.findByCpf(cpf);
    }

    // cadastrando um novo tutor. Se já existir, devolvemos o existente
    public Owner findOrCreateOwnerFromDTO(OwnerDTO dto) {
        return ownerRepository.findByCpf(dto.getCpf())
                .map(existing -> updateOwnerData(existing, dto))
                .orElseGet(() -> ownerRepository.save(petMapper.toOwner(dto)));
    }

    // Permitimos atualizar nome, e-mail e telefone — mas não o CPF (que é fixo como
    // identidade).
    public Owner updateOwnerData(Owner existing, OwnerDTO newData) {
        existing.setName(newData.getName());
        existing.setEmail(newData.getEmail());
        existing.setPhone(newData.getPhone());
        return ownerRepository.save(existing); // salvamos as mudanças no banco
    }

    /**
     * excluir um tutor, mas tendo cuidado com os pets dele:
     *
     * 1️⃣ Se o pet tem **outros tutores**, ele continua existindo.
     * 2️⃣ Se o pet **fica sozinho**, ou seja, sem nenhum tutor, ele também é
     * removido.
     *
     * Tudo isso acontece dentro de uma "transação", ou seja, ou tudo dá certo ou
     * nada é feito.
     */
    @Transactional
    public void deleteOwnerById(Long ownerId) {
        // Primeiro, procuramos o tutor no banco
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Tutor not found"));

        // Pegamos todos os pets desse tutor
        Set<Pet> pets = owner.getPets();

        // Para cada pet do tutor...
        for (Pet pet : pets) {
            // Removemos o vínculo entre o pet e esse tutor
            pet.getOwners().remove(owner);

            // Se o pet ficou sem nenhum tutor, deletamos ele também
            if (pet.getOwners().isEmpty()) {
                petRepository.delete(pet);
            }
        }

        // Por fim, deletamos o tutor
        ownerRepository.delete(owner);
    }

    // Método para alterar o CPF de um tutor
    @Transactional
    public Owner changeCpf(String oldCpf, String newCpf) {
        Owner existing = ownerRepository.findByCpf(oldCpf)
                .orElseThrow(() -> new OwnerNotFoundException("Tutor not found with CPF: " + oldCpf));

        // Confere se já existe um dono com o novo CPF
        if (ownerRepository.findByCpf(newCpf).isPresent()) {
            throw new IllegalArgumentException("Já existe um tutor cadastrado com o novo CPF: " + newCpf);
        }

        existing.setCpf(newCpf);
        return ownerRepository.save(existing);
    }

}
