package com.ms.cadastro.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ms.cadastro.dto.OwnerDTO;
import com.ms.cadastro.dto.PetDTO;
import com.ms.cadastro.enums.Species;
import com.ms.cadastro.exception.BreedNotFoundException;
import com.ms.cadastro.exception.InvalidPetUpdateException;
import com.ms.cadastro.exception.OwnerCpfMismatchException;
import com.ms.cadastro.exception.PetAlreadyExistsException;
import com.ms.cadastro.exception.PetNotFoundException;
import com.ms.cadastro.mapper.PetMapper;
import com.ms.cadastro.messaging.PetProducer;
import com.ms.cadastro.model.Owner;
import com.ms.cadastro.model.Pet;
import com.ms.cadastro.repository.PetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final OwnerService ownerService;
    private final BreedService breedService;
    private final PetMapper petMapper;
    private final PetProducer petProducer;

    // Criação de novo pet
    @Transactional
    public PetDTO create(PetDTO dto) {
        validateOwnerPresence(dto);
        Set<Owner> resolvedOwners = resolveOwners(dto.getOwners());
        checkDuplicatePet(dto, resolvedOwners);
        validateBreed(dto);

        Pet pet = buildPetEntity(dto, resolvedOwners);
        Pet saved = petRepository.save(pet);
        petProducer.sendCreated(petMapper.toPetEventDTO(saved));

        return petMapper.toPetDTO(saved);
    }

    // Atualização de pet existente
    @Transactional
    public PetDTO update(Long id, PetDTO dto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));

        validateBreed(dto);
        updatePetFieldsRestrictions(pet, dto);
        pet.setOwners(resolveUpdatedOwners(pet, dto.getOwners()));

        Pet updated = petRepository.save(pet);
        petProducer.sendUpdated(petMapper.toPetEventDTO(updated));

        return petMapper.toPetDTO(updated);
    }

    // Atualiza os campos do pet com validações de integridade.
    // Só permite alterar nome OU data de nascimento — mas não ambos ao mesmo tempo.
    private void updatePetFieldsRestrictions(Pet pet, PetDTO dto) {
        boolean nameChanged = !pet.getName().equals(dto.getName());
        boolean birthDateChanged = !pet.getBirthDate().equals(dto.getBirthDate());

        if (nameChanged && birthDateChanged) {
            throw new InvalidPetUpdateException(
                    "You can only change the pet's name OR birth date at a time, not both.");
        }

        // Alterações permitidas
        pet.setName(dto.getName());
        pet.setBirthDate(dto.getBirthDate());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setColor(dto.getColor());
        pet.setWeight(dto.getWeight());
        pet.setDescription(dto.getDescription());
        pet.setTemperament(dto.getTemperament());
        pet.setLastVaccinationDate(dto.getLastVaccinationDate());

        if ((dto.getImageUrl() == null || dto.getImageUrl().isBlank())
                && !"SRD".equalsIgnoreCase(dto.getBreed())) {
            String image = breedService.getImageFromApi(dto.getSpecies(), dto.getBreed());
            pet.setImageUrl(image);
        } else {
            pet.setImageUrl(dto.getImageUrl());
        }
    }

    // Valida presença de pelo menos um tutor
    private void validateOwnerPresence(PetDTO dto) {
        if (dto.getOwners() == null || dto.getOwners().isEmpty()) {
            throw new IllegalArgumentException("Pet must have at least one owner.");
        }
    }

    // Verifica se já existe um pet com mesmo nome, data de nascimento e tutor
    private void checkDuplicatePet(PetDTO dto, Set<Owner> owners) {
        for (Owner owner : owners) {
            boolean exists = petRepository.existsByNameAndBirthDateAndOwner(
                    dto.getName(), dto.getBirthDate(), owner);
            if (exists) {
                throw new PetAlreadyExistsException(
                        "Pet with the same name and birth date already exists for owner: " + owner.getName());
            }
        }
    }

    // Constrói a entidade Pet com os donos resolvidos
    private Pet buildPetEntity(PetDTO dto, Set<Owner> resolvedOwners) {
        Pet pet = petMapper.toEntity(dto);
        pet.setOwners(resolvedOwners);
        return pet;
    }

    // Validação de raça e atribuição de imagem, se necessário
    private void validateBreed(PetDTO dto) {
        if (!"SRD".equalsIgnoreCase(dto.getBreed())) {
            boolean valid = breedService.isValidBreed(dto.getBreed(), dto.getSpecies());
            if (!valid) {
                throw new BreedNotFoundException("Breed not found: " + dto.getBreed() + " (" + dto.getSpecies() + ")");
            }

            if (dto.getImageUrl() == null || dto.getImageUrl().isBlank()) {
                String image = breedService.getImageFromApi(dto.getSpecies(), dto.getBreed());
                dto.setImageUrl(image);
            }
        }
    }

    // Resolve lista de donos com base nos DTOs recebidos
    private Set<Owner> resolveOwners(Set<OwnerDTO> dtos) {
        return dtos.stream()
                .map(ownerService::findOrCreateOwnerFromDTO)
                .collect(Collectors.toSet());
    }

    // Atualiza os tutores com validação de CPF
    private Set<Owner> resolveUpdatedOwners(Pet pet, Set<OwnerDTO> dtos) {
        Set<Owner> updatedOwners = new HashSet<>();
        for (OwnerDTO incomingDTO : dtos) {
            Optional<Owner> matchingCurrentOwner = pet.getOwners().stream()
                    .filter(existing -> existing.getName().equalsIgnoreCase(incomingDTO.getName()) &&
                            existing.getEmail().equalsIgnoreCase(incomingDTO.getEmail()) &&
                            existing.getPhone().equalsIgnoreCase(incomingDTO.getPhone()))
                    .findFirst();

            if (matchingCurrentOwner.isPresent()) {
                Owner current = matchingCurrentOwner.get();
                if (!current.getCpf().equals(incomingDTO.getCpf())) {
                    throw new OwnerCpfMismatchException(
                            "CPF cannot be changed via this operation. To update the tutor's CPF, please use the dedicated endpoint: PATCH /owners/{oldCpf}/change-cpf.");
                }
                updatedOwners.add(current);
            } else {
                Owner resolved = ownerService.findOrCreateOwnerFromDTO(incomingDTO);
                updatedOwners.add(resolved);
            }
        }
        return updatedOwners;
    }

    public void deleteById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));
        petRepository.delete(pet);
    }

    public List<PetDTO> findAll() {
        return petRepository.findAll().stream()
                .map(petMapper::toPetDTO)
                .collect(Collectors.toList());
    }

    public List<PetDTO> search(Species species, String breed) {
        return petRepository.findBySpeciesAndBreedFlexible(species, breed).stream()
                .map(petMapper::toPetDTO)
                .collect(Collectors.toList());
    }

    public Optional<PetDTO> findById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::toPetDTO);
    }
}
