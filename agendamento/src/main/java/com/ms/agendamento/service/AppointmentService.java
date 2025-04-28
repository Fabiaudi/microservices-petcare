package com.ms.agendamento.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.agendamento.dto.AppointmentDTO;
import com.ms.agendamento.dto.AppointmentResponseDTO;
import com.ms.agendamento.dto.PetEventDTO;
import com.ms.agendamento.dto.PetInfoRequestEventDTO;
import com.ms.agendamento.enums.AppointmentType;
import com.ms.agendamento.exception.AppointmentNotFoundException;
import com.ms.agendamento.exception.OutsideBusinessHoursException;
import com.ms.agendamento.exception.TimeSlotUnavailableException;
import com.ms.agendamento.mapper.AppointmentMapper;
import com.ms.agendamento.messaging.producer.AppointmentProducer;
import com.ms.agendamento.messaging.producer.PetInfoRequestProducer;
import com.ms.agendamento.model.Appointment;
import com.ms.agendamento.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int MAX_APPOINTMENTS_PER_SLOT = 3;
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(18, 0);
    private static final int SLOT_INTERVAL_MINUTES = 30;
    private static final int DEFAULT_DAYS_AHEAD = 15;

    private final AppointmentRepository appointmentRepository;
    private final AppointmentProducer appointmentProducer;
    private final PetInfoRequestProducer petInfoRequestProducer;
    private final AppointmentMapper mapper;

    private final ConcurrentHashMap<Long, AppointmentDTO> pendingRequests = new ConcurrentHashMap<>();

    @Transactional
    public void requestManualAppointment(AppointmentDTO dto) {
        pendingRequests.put(dto.getPetId(), dto);
        petInfoRequestProducer.sendRequest(new PetInfoRequestEventDTO(dto.getPetId()));
    }

    @Transactional
    public void handlePetInfoResponse(PetEventDTO pet) {
        AppointmentDTO pending = pendingRequests.remove(pet.getId());
        if (pending != null) {
            validateSlotAvailability(pending.getDateTime());

            Appointment appointment = buildAppointment(pet, pending, false);
            appointmentRepository.save(appointment);
            appointmentProducer.publish(mapper.toResponseDTO(appointment));
        }
    }

    @Transactional
    public void handleAutomaticAppointments(PetEventDTO pet) {
        Set<AppointmentType> types = determineAppointmentTypes(pet);
        LocalDateTime appointmentDate = findNextAvailableSlot(LocalDateTime.now().plusDays(DEFAULT_DAYS_AHEAD));

        Appointment appointment = buildAppointment(pet, appointmentDate, types, true);
        appointmentRepository.save(appointment);
        appointmentProducer.publish(mapper.toResponseDTO(appointment));
    }

    @Transactional
    public void updateAppointmentsIfNecessary(PetEventDTO pet) {
        List<Appointment> appointments = appointmentRepository.findFiltered(null, LocalDateTime.now()).stream()
                .filter(app -> app.getPetId().equals(pet.getId()))
                .toList();

        Set<AppointmentType> newTypes = determineAppointmentTypes(pet);
        String newPetName = pet.getName();
        String newOwnerEmail = pet.getOwners().iterator().next().getEmail();

        for (Appointment app : appointments) {
            boolean changed = false;

            if (!app.getPetName().equals(newPetName)) {
                app.setPetName(newPetName);
                changed = true;
            }

            if (!app.getOwnerEmail().equals(newOwnerEmail)) {
                app.setOwnerEmail(newOwnerEmail);
                changed = true;
            }

            if (app.isAutomatic() && (!app.getTypes().equals(newTypes))) {
                app.setTypes(newTypes);
                changed = true;
            }

            if (changed) {
                appointmentRepository.save(app);
                if (app.isAutomatic()) {
                    appointmentProducer.publishUpdated(mapper.toResponseDTO(app));
                }
            }
        }
    }

    private Set<AppointmentType> determineAppointmentTypes(PetEventDTO pet) {
        Set<AppointmentType> types = new HashSet<>();
        LocalDate birth = pet.getBirthDate();
        LocalDate lastVaccine = pet.getLastVaccinationDate();

        if (birth.isAfter(LocalDate.now().minusMonths(6))) {
            types.add(AppointmentType.INITIAL_VACCINATION);
        } else {
            if (lastVaccine != null && lastVaccine.isBefore(LocalDate.now().minusYears(1))) {
                types.add(AppointmentType.ANNUAL_VACCINATION);
            }
            types.add(AppointmentType.CHECKUP);
        }

        return types;
    }

    private void validateSlotAvailability(LocalDateTime dateTime) {
        LocalDateTime normalized = dateTime.withSecond(0).withNano(0);
        if (!isBusinessHour(normalized)) {
            throw new OutsideBusinessHoursException();
        }

        long count = appointmentRepository.countByDateTime(normalized);
        if (count >= MAX_APPOINTMENTS_PER_SLOT) {
            throw new TimeSlotUnavailableException();
        }
    }

    private LocalDateTime findNextAvailableSlot(LocalDateTime start) {
        LocalDateTime candidate = start.withHour(START_TIME.getHour())
                .withMinute(START_TIME.getMinute())
                .withSecond(0)
                .withNano(0);

        while (true) {
            if (isBusinessHour(candidate)) {
                long count = appointmentRepository.countByDateTime(candidate);
                if (count < MAX_APPOINTMENTS_PER_SLOT) {
                    return candidate;
                }
            }
            candidate = candidate.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
    }

    private boolean isBusinessHour(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        return !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                && !time.isBefore(START_TIME)
                && time.isBefore(END_TIME);
    }

    private Appointment buildAppointment(PetEventDTO pet, AppointmentDTO dto, boolean automatic) {
        return Appointment.builder()
                .petId(pet.getId())
                .petName(pet.getName())
                .ownerEmail(pet.getOwners().iterator().next().getEmail())
                .types(dto.getTypes())
                .dateTime(dto.getDateTime())
                .automatic(automatic)
                .confirmed(false)
                .build();
    }

    private Appointment buildAppointment(PetEventDTO pet, LocalDateTime dateTime, Set<AppointmentType> types, boolean automatic) {
        return Appointment.builder()
                .petId(pet.getId())
                .petName(pet.getName())
                .ownerEmail(pet.getOwners().iterator().next().getEmail())
                .types(types)
                .dateTime(dateTime)
                .automatic(automatic)
                .confirmed(false)
                .build();
    }

    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        return mapper.toResponseDTO(appointment);
    }

    @Transactional
    public List<AppointmentResponseDTO> search(AppointmentType type, LocalDateTime startDate) {
        return appointmentRepository.findFiltered(type, startDate).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deleteAppointment(Long id, boolean publishEvent) {
        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isEmpty()) {
            System.out.println("[AVISO] Tentativa de deletar agendamento ID " + id + ", mas já estava excluído.");
            return;
        }
        Appointment appointment = optional.get();
        AppointmentResponseDTO dto = mapper.toResponseDTO(appointment);
        appointmentRepository.delete(appointment);

        if (publishEvent) {
            appointmentProducer.publishCancelled(dto);
        }
    }

    @Transactional
    public boolean deleteIfExists(Long id) {
        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isEmpty()) return false;

        appointmentRepository.delete(optional.get());
        return true; // Não publica evento para evitar loop
    }

    @Transactional
    public void confirm(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setConfirmed(true);
        appointmentRepository.save(appointment);

        System.out.println("[AGENDAMENTO] Confirmado com sucesso: ID " + id);
    }
}
