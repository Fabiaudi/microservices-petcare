package com.ms.notificacao.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ms.notificacao.dto.AppointmentResponseDTO;

@Service
public class AppointmentConfirmationService {

    // Estrutura para guardar o token junto com o agendamento
    private record TokenizedAppointment(String token, AppointmentResponseDTO appointment) {}

    private final ConcurrentHashMap<Long, TokenizedAppointment> cache = new ConcurrentHashMap<>();

    // Armazena o agendamento e gera token
    public String storeWithToken(AppointmentResponseDTO dto) {
        String token = UUID.randomUUID().toString();
        cache.put(dto.getId(), new TokenizedAppointment(token, dto));
        return token;
    }

    // Valida token e retorna o agendamento correspondente
    public Optional<AppointmentResponseDTO> validateToken(Long id, String token) {
        TokenizedAppointment entry = cache.get(id);
        if (entry != null && entry.token().equals(token)) {
            return Optional.of(entry.appointment());
        }
        return Optional.empty();
    }

    // Método auxiliar (usado em testes ou callbacks sem token — evite em produção)
    public Optional<AppointmentResponseDTO> findAppointmentById(Long id) {
        TokenizedAppointment entry = cache.get(id);
        return entry != null ? Optional.of(entry.appointment()) : Optional.empty();
    }
}
