package com.ms.cadastro.mapper;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ms.cadastro.dto.BreedDTO;
import com.ms.cadastro.dto.OwnerDTO;
import com.ms.cadastro.dto.PetDTO;
import com.ms.cadastro.dto.PetEventDTO;
import com.ms.cadastro.external.PetApiDto;
import com.ms.cadastro.model.Breed;
import com.ms.cadastro.model.Owner;
import com.ms.cadastro.model.Pet;

@Mapper(componentModel = "spring")
public interface PetMapper {

    // PET <-> PetDTO
    PetDTO toPetDTO(Pet pet);
    Pet toEntity(PetDTO dto);

    // PET -> PetEventDTO (usado em eventos)
    PetEventDTO toPetEventDTO(Pet pet);

    // OWNER <-> OwnerDTO
    @Mapping(target = "pets", ignore = true)
    OwnerDTO toOwnerDTO(Owner owner);
    @Mapping(target = "pets", ignore = true)
    Owner toOwner(OwnerDTO dto);

    // BREED <-> BreedDTO
    BreedDTO toBreedDTO(Breed breed);
    Breed toBreed(BreedDTO dto); 

    // Set<OWNER> <-> Set<OwnerDTO>
    default Set<OwnerDTO> toOwnerDTOSet(Set<Owner> owners) {
        if (owners == null) return Collections.emptySet();
        return owners.stream()
                .map(this::toOwnerDTO)
                .collect(Collectors.toSet());
    }

    default Set<Owner> toOwnerSet(Set<OwnerDTO> owners) {
        if (owners == null) return Collections.emptySet();
        return owners.stream()
                .map(this::toOwner)
                .collect(Collectors.toSet());
    }

    // PetApiDto → BreedDTO
    @Mapping(target = "imageUrl", source = "image.url", ignore = true)
    @Mapping(target = "species", ignore = true)
    BreedDTO toBreedDTO(PetApiDto dto);
}
