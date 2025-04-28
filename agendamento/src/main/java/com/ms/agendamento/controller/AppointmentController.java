package com.ms.agendamento.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ms.agendamento.dto.AppointmentDTO;
import com.ms.agendamento.dto.AppointmentResponseDTO;
import com.ms.agendamento.enums.AppointmentType;
import com.ms.agendamento.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Controller", description = "Handles operations related to pet care appointments")
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @Operation(summary = "Create manual appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Appointment request accepted")
    })
    public ResponseEntity<Void> createManual(@RequestBody AppointmentDTO dto) {
        service.requestManualAppointment(dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping
    @Operation(summary = "List all appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search appointments by type and/or start date")
    @ApiResponse(responseCode = "200", description = "Filtered appointments returned successfully")
    public ResponseEntity<List<AppointmentResponseDTO>> searchAppointments(
            @RequestParam(required = false) AppointmentType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {

        return ResponseEntity.ok(service.search(type, startDate));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteAppointment(id, true);
        return ResponseEntity.noContent().build();
    }
}
