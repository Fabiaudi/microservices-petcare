package com.ms.cadastro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.cadastro.dto.BreedDTO;
import com.ms.cadastro.enums.Species;
import com.ms.cadastro.exception.BreedNotFoundException;
import com.ms.cadastro.service.BreedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
@Tag(name = "Breed Controller", description = "Handles breed queries and synchronizations")
public class BreedController {

    private final BreedService breedService;

    @GetMapping
    @Operation(summary = "List all breeds")
    public ResponseEntity<List<BreedDTO>> findAll() {
        return ResponseEntity.ok(breedService.getAllBreeds());
    }

    @GetMapping("/species/{species}")
    @Operation(summary = "Find breeds by species")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Breeds found"),
        @ApiResponse(responseCode = "400", description = "Invalid species")
    })
    public ResponseEntity<List<BreedDTO>> getBreedsBySpecies(@PathVariable String species) {
        Species speciesEnum = Species.valueOf(species.toUpperCase()); // Lança IllegalArgumentException se inválido

        List<BreedDTO> breeds = breedService.getAllBreeds().stream()
                .filter(breed -> breed.getSpecies() == speciesEnum)
                .toList();

        if (breeds.isEmpty()) {
            throw new BreedNotFoundException("No breeds found for species: " + species);
        }

        return ResponseEntity.ok(breeds);
    }

    @GetMapping("/{name}")
    @Operation(summary = "Find breeds by name", description = "Returns all breeds that match the given name, regardless of species.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Breeds successfully found."),
        @ApiResponse(responseCode = "404", description = "No breeds found with the given name.")
    })
    public ResponseEntity<List<BreedDTO>> getBreedsByName(
            @Parameter(description = "Name of the breed") @PathVariable String name) {
        List<BreedDTO> breeds = breedService.findAllByName(name);
        return ResponseEntity.ok(breeds);
    }

    @PostMapping("/sync")
    @Operation(summary = "Synchronize all breeds", description = "Updates breeds of all species from external APIs.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "All breeds updated!"))
    public ResponseEntity<?> syncAllBreeds() {
        breedService.updateBreedsFromAPI(Species.DOG);
        breedService.updateBreedsFromAPI(Species.CAT);
        return ResponseEntity.ok(Map.of("message", "All breeds successfully updated!"));
    }

    @PostMapping("/sync/{species}")
    @Operation(summary = "Synchronize breeds with external API", description = "Updates pet breeds based on the external API.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Breeds successfully updated!"))
    public ResponseEntity<?> syncBreeds(@PathVariable Species species) {
        breedService.updateBreedsFromAPI(species);
        return ResponseEntity.ok(Map.of("message", "Breeds successfully updated!"));
    }
}
