package com.ms.cadastro.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ms.cadastro.enums.Species;
import com.ms.cadastro.model.Owner;
import com.ms.cadastro.model.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

        @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.owners WHERE p.id = :id")
        Optional<Pet> findByIdWithOwners(@Param("id") Long id);

        @Query("SELECT p FROM Pet p WHERE " +
                        "(:species IS NULL OR p.species = :species) AND " +
                        "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%')))")
        List<Pet> findBySpeciesAndBreedFlexible(@Param("species") Species species, @Param("breed") String breed);

        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
                        "FROM Pet p JOIN p.owners o " +
                        "WHERE p.name = :name AND p.birthDate = :birthDate AND o = :owner")
        boolean existsByNameAndBirthDateAndOwner(String name, LocalDate birthDate, Owner owner);

}
