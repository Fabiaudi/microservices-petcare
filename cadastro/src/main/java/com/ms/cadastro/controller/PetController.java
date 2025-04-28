package com.ms.cadastro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ms.cadastro.dto.PetDTO;
import com.ms.cadastro.enums.Species;
import com.ms.cadastro.exception.PetNotFoundException;
import com.ms.cadastro.service.PetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Handles operations related to pet registration, updates and queries")
public class PetController {

    private final PetService petService;

    @PostMapping
    @Operation(summary = "Register a new pet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pet created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Pet already exists")
    })
    public ResponseEntity<PetDTO> create(@RequestBody @Valid PetDTO petDto) {
        PetDTO created = petService.create(petDto);
        // Publica evento pet_created no RabbitMQ após salvar o pet
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing pet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet updated successfully"),
            @ApiResponse(responseCode = "404", description = "Pet not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody @Valid PetDTO petDto) {
        PetDTO updated = petService.update(id, petDto);
        // Publica evento pet_updated no RabbitMQ após atualizar o pet
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Se o pet não existir, uma exceção será lançada
        petService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all pets")
    @ApiResponse(responseCode = "200", description = "List of pets returned successfully")
    public ResponseEntity<List<PetDTO>> findAll() {
        return ResponseEntity.ok(petService.findAll());
    }

    @GetMapping("/search")
    @Operation(summary = "Search pets by species and/or breed")
    @ApiResponse(responseCode = "200", description = "Filtered list of pets returned successfully")
    public ResponseEntity<List<PetDTO>> search(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed) {

        Species speciesEnum = null;
        if (species != null && !species.isBlank()) {
            try {
                speciesEnum = Species.valueOf(species.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(List.of()); 
            }
        }

        return ResponseEntity.ok(petService.search(speciesEnum, breed));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet found"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<PetDTO> findById(@PathVariable Long id) {
        PetDTO pet = petService.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found with ID: " + id));
        return ResponseEntity.ok(pet);
    }
}
