package com.ms.agendamento.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.ms.agendamento.enums.AppointmentType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private String petName;

    @Column(nullable = false)
    private String ownerEmail;

    @ElementCollection(targetClass = AppointmentType.class)
    @CollectionTable(name = "tb_appointment_types", joinColumns = @JoinColumn(name = "appointment_id"))
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Set<AppointmentType> types;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private boolean automatic;

    private boolean confirmed;

    private String notes;
}
