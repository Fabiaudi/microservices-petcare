package com.ms.cadastro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.cadastro.dto.CpfChangeRequestDTO;
import com.ms.cadastro.dto.OwnerDTO;
import com.ms.cadastro.exception.OwnerCpfMismatchException;
import com.ms.cadastro.exception.OwnerNotFoundException;
import com.ms.cadastro.mapper.PetMapper;
import com.ms.cadastro.model.Owner;
import com.ms.cadastro.service.OwnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Owner Controller", description = "Handles CRUD operations related to pet owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PetMapper petMapper;

    @GetMapping
    @Operation(summary = "List all owners")
    public ResponseEntity<List<OwnerDTO>> findAll() {
        List<OwnerDTO> owners = ownerService.findAll();
        return ResponseEntity.ok(owners);
    }

    // Busca um tutor pelo CPF e retorna os dados formatados
    @GetMapping("/{cpf}")
    @Operation(summary = "Get an owner by CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner found"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    public ResponseEntity<OwnerDTO> findByCpf(@PathVariable String cpf) {
        OwnerDTO dto = ownerService.findByCpf(cpf)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with CPF: " + cpf));
        return ResponseEntity.ok(dto);
    }

    // Atualiza os dados de um tutor com base no CPF
    @PutMapping("/{cpf}")
    @Operation(summary = "Update owner data by CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owner updated successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    public ResponseEntity<OwnerDTO> update(@PathVariable String cpf, @RequestBody @Valid OwnerDTO dto) {
        if (!cpf.equals(dto.getCpf())) {
                throw new OwnerCpfMismatchException(
                        "CPF cannot be changed via this operation. To update the tutor's CPF, please use the dedicated endpoint: PATCH /owners/{oldCpf}/change-cpf.");
        }
        Owner existing = ownerService.findEntityByCpf(cpf)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with CPF: " + cpf));

        Owner updated = ownerService.updateOwnerData(existing, dto);
        return ResponseEntity.ok(petMapper.toOwnerDTO(updated));
    }

    @PatchMapping("/{oldCpf}/change-cpf")
    @Operation(summary = "Change owner's CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CPF updated successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "400", description = "Invalid CPF change")
    })
    public ResponseEntity<OwnerDTO> changeCpf(
            @PathVariable String oldCpf,
            @RequestBody @Valid CpfChangeRequestDTO request) {

        Owner updated = ownerService.changeCpf(oldCpf, request.getNewCpf());
        return ResponseEntity.ok(petMapper.toOwnerDTO(updated));
    }

    // Remove um tutor com base no ID
    // Se o pet ficar sem tutores, ele também é excluído
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owner by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Owner deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        ownerService.deleteOwnerById(id); // já lança exceção se não encontrado
        return ResponseEntity.noContent().build();
    }
}
