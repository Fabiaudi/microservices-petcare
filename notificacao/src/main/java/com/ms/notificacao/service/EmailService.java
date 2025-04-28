package com.ms.notificacao.service;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ms.notificacao.dto.AppointmentResponseDTO;
import com.ms.notificacao.dto.OwnerDTO;
import com.ms.notificacao.dto.PetEventDTO;
import com.ms.notificacao.messaging.producer.AppointmentConfirmationProducer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppointmentConfirmationService confirmationService;
    private final AppointmentConfirmationProducer confirmationProducer;

    private final String FROM_EMAIL = "fabi.haehling@gmail.com";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void sendWelcomeEmails(PetEventDTO pet) {
        Set<OwnerDTO> owners = pet.getOwners();
        for (OwnerDTO owner : owners) {
            String subject = "🐾 Bem-vindo(a), " + owner.getName() + "!";
            String htmlBody = buildWelcomeEmailHtml(pet, owner.getName());
            sendHtmlEmail(owner.getEmail(), subject, htmlBody);
        }
    }

    public void sendAppointmentConfirmation(AppointmentResponseDTO dto) {
        String subject = "🔔 Confirmação de Agendamento - " + dto.getPetName();
        String token = confirmationService.storeWithToken(dto); // CORRETO AGORA
        String htmlBody = buildAppointmentHtml(dto, token);
        sendHtmlEmail(dto.getOwnerEmail(), subject, htmlBody);
    }

    public void sendAppointmentUpdate(AppointmentResponseDTO dto) {
        String subject = "🔄 Atualização no Agendamento de " + dto.getPetName();
        String token = confirmationService.storeWithToken(dto);
        String htmlBody = buildAppointmentHtml(dto, token);
        sendHtmlEmail(dto.getOwnerEmail(), subject, htmlBody);
    }

    public void sendAppointmentCancellation(AppointmentResponseDTO dto) {
        String subject = "❌ Agendamento Cancelado - " + dto.getPetName();
        String htmlBody = buildCancellationHtml(dto);
        sendHtmlEmail(dto.getOwnerEmail(), subject, htmlBody);

        // Só notifica o serviço de Agendamento se for cancelamento por link
        if (!dto.isConfirmed()) {
            System.out.println("[NOTIFICAÇÃO] Cancelamento manual detectado, nenhum evento será reenviado.");
        } else {
            System.out.println("[NOTIFICAÇÃO] Cancelamento por link detectado, evento será enviado.");
            confirmationProducer.sendCancellation(dto);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            System.out.println("[EMAIL HTML ENVIADO] Para: " + to + " | Assunto: " + subject);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail HTML para: " + to, e);
        }
    }

    private String buildWelcomeEmailHtml(PetEventDTO pet, String ownerName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Olá, ").append(ownerName).append("! 🐾</h2>");
        sb.append("<p>Seu pet foi cadastrado com sucesso no sistema da Lambidão Pet!</p>");
        sb.append("<ul>");
        sb.append("<li><strong>Nome:</strong> ").append(pet.getName()).append("</li>");
        sb.append("<li><strong>Nascimento:</strong> ").append(pet.getBirthDate().format(DATE_FORMAT)).append("</li>");
        sb.append("<li><strong>Espécie:</strong> ").append(pet.getSpecies()).append("</li>");
        sb.append("<li><strong>Raça:</strong> ").append(pet.getBreed()).append("</li>");
        sb.append("<li><strong>Cor:</strong> ").append(pet.getColor()).append("</li>");
        sb.append("<li><strong>Peso:</strong> ").append(pet.getWeight()).append(" kg</li>");
        sb.append("</ul>");
        if (pet.getImageUrl() != null && !pet.getImageUrl().isBlank()) {
            sb.append("<p><img src='").append(pet.getImageUrl()).append("' width='300' /></p>");
        }
        sb.append("<p>Seja bem-vindo(a) à nossa família 💙</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildAppointmentHtml(AppointmentResponseDTO dto, String token) {
        String tipo = dto.isAutomatic() ? "automático" : "manual";
        String tipos = dto.getTypes().stream()
                .map(t -> "✔️ " + t.name().replace("_", " ").toLowerCase())
                .reduce("", (a, b) -> a + "<br>" + b);

        String gatewayBaseUrl = "http://localhost:9191";
        String confirmUrl = gatewayBaseUrl + "/email/appointments/confirm?id=" + dto.getId() + "&token=" + token;
        String cancelUrl = gatewayBaseUrl + "/email/appointments/cancel?id=" + dto.getId() + "&token=" + token;

        return String.format("""
                <html>
                <body>
                    <h2>Olá! 🐶</h2>
                    <p>Temos novidades sobre o agendamento do(a) <strong>%s</strong></p>
                    <ul>
                        <li><strong>Data e hora:</strong> %s</li>
                        <li><strong>Tipo:</strong> %s</li>
                        <li><strong>Serviços:</strong><br>%s</li>
                    </ul>
                    <p>
                        ✅ <a href="%s">Confirmar Agendamento</a><br>
                        ❌ <a href="%s">Cancelar Agendamento</a>
                    </p>
                    <p>Com carinho,<br>Equipe Lambidão Pet 💙</p>
                </body>
                </html>
                """,
                dto.getPetName(),
                dto.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                tipo,
                tipos,
                confirmUrl,
                cancelUrl);
    }

    private String buildCancellationHtml(AppointmentResponseDTO dto) {
        return String.format("""
                <html>
                <body>
                    <h2>Agendamento Cancelado ❌</h2>
                    <p>O agendamento do(a) <strong>%s</strong> para o dia <strong>%s</strong> foi cancelado.</p>
                    <p>⚠️ Se desejar reagendar, entre em contato conosco.</p>
                    <p>Com carinho,<br>Equipe Lambidão Pet 💙</p>
                </body>
                </html>
                """,
                dto.getPetName(),
                dto.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
}
