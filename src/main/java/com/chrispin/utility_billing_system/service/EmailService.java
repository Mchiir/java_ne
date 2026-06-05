package rw.utility.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from}")
    private String from;

    /**
     * Sends a plain-text email. Best-effort: failures are logged but never propagate,
     * so email problems can't break billing/payment flows.
     *
     * @return true if the message was dispatched.
     */
    public boolean send(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("[mail disabled] would send to {} | {}", to, subject);
            return false;
        }
        if (!StringUtils.hasText(to)) {
            log.warn("Skipping email '{}' — no recipient address.", subject);
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} | {}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {} ({}): {}", to, subject, e.getMessage());
            return false;
        }
    }
}
