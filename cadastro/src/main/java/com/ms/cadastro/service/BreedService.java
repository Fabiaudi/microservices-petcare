package com.ms.cadastro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.cadastro.dto.BreedDTO;
import com.ms.cadastro.enums.Species;
import com.ms.cadastro.external.PetApi;
import com.ms.cadastro.mapper.PetMapper;
import com.ms.cadastro.model.Breed;
import com.ms.cadastro.repository.BreedRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;
    private final PetApi petApi;
    private final PetMapper petMapper;

    // Roda na inicialização da aplicação
    @PostConstruct
    @Transactional
    public void syncBreedsOnStartup() {
        if (breedRepository.count() == 0) {
            updateBreedsFromAPI(Species.DOG);
            updateBreedsFromAPI(Species.CAT);
        }
    }

    // Busca ou cria uma raça com base no nome e espécie
    private Breed getOrCreateBreed(String name, Species species, String imageUrl) {
        return breedRepository.findAllByName(name).stream()
                .filter(existingBreed -> existingBreed.getSpecies() == species)
                .findFirst()
                .map(existingBreed -> {
                    existingBreed.setImageUrl(imageUrl); // Atualiza imagem se já existe
                    return existingBreed;
                })
                .orElseGet(() -> {
                    Breed newBreed = new Breed();
                    newBreed.setName(name);
                    newBreed.setSpecies(species);
                    newBreed.setImageUrl(imageUrl);
                    return newBreed;
                });
    }

    // Atualiza as raças de uma espécie a partir da API externa
    @Transactional
    public void updateBreedsFromAPI(Species species) {
        petApi.getBreeds(species).stream()
                .map(dto -> {
                    String imageUrl = petApi.getImageFromBreed(species, dto.getName());
                    return getOrCreateBreed(dto.getName(), species, imageUrl);
                })
                .forEach(breedRepository::save);

        // Garante que a raça SRD está presente
        for (Species sp : Species.values()) {
            boolean srdExists = breedRepository.findAllByName("SRD").stream()
                    .anyMatch(b -> b.getSpecies() == sp);
            if (!srdExists) {
                Breed srd = new Breed();
                srd.setName("SRD");
                srd.setSpecies(sp);
                breedRepository.save(srd);
            }
        }
    }

    // Retorna todas as raças como DTOs
    public List<BreedDTO> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(petMapper::toBreedDTO)
                .toList();
    }

    // Busca raças por nome
    public List<BreedDTO> findAllByName(String name) {
        List<Breed> breeds = breedRepository.findAllByName(name);

        if (breeds.isEmpty()) {
            throw new EntityNotFoundException("No breeds found with name: " + name);
        }

        return breeds.stream()
                .map(petMapper::toBreedDTO)
                .toList();
    }

    public boolean isValidBreed(String name, Species species) {
        if ("SRD".equalsIgnoreCase(name)) return true;
    
        return breedRepository.findAllByName(name).stream()
                .anyMatch(b -> b.getSpecies() == species);
    }    
    
    public String getImageFromApi(Species species, String breed) {
        return petApi.getImageFromBreed(species, breed);
    }
}
