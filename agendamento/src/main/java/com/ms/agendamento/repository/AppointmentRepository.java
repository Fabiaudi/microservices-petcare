package com.ms.agendamento.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ms.agendamento.enums.AppointmentType;
import com.ms.agendamento.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Busca com filtros flexíveis por tipo e data
    @Query("SELECT DISTINCT a FROM Appointment a LEFT JOIN FETCH a.types t " +
       "WHERE (:type IS NULL OR :type MEMBER OF a.types) " +
       "AND (:startDate IS NULL OR a.dateTime >= :startDate)")
    List<Appointment> findFiltered(@Param("type") AppointmentType type,
            @Param("startDate") LocalDateTime startDate);

    // Conta quantos agendamentos existem em um slot específico
    long countByDateTime(LocalDateTime dateTime);
}
