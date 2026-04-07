package edu.ntnu.idi.idatt2105.backend.common.email;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmailService {
  private final RestTemplate restTemplate = new RestTemplate();
  private final boolean resendEnabled;
  private final String resendApiKey;
  private final String resendFromEmail;
  private final String resendBaseUrl;

  public EmailService(
      @Value("${email.resend.enabled:false}") boolean resendEnabled,
      @Value("${email.resend.api-key:}") String resendApiKey,
      @Value("${email.resend.from-email:no-reply@example.com}") String resendFromEmail,
      @Value("${email.resend.base-url:https://api.resend.com}") String resendBaseUrl) {
    this.resendEnabled = resendEnabled;
    this.resendApiKey = resendApiKey;
    this.resendFromEmail = resendFromEmail;
    this.resendBaseUrl = resendBaseUrl;
  }

  public void sendEmail(String to, String subject, String body) {
    sendResendEmail(to, subject, body, null);
  }

  public void sendHtmlEmail(String to, String subject, String htmlBody) {
    sendResendEmail(to, subject, null, htmlBody);
  }

  private void sendResendEmail(String to, String subject, String textBody, String htmlBody) {
    if (!resendEnabled) {
      log.info("Resend disabled. Skipping email to {} with subject '{}'", to, subject);
      return;
    }

    if (resendApiKey == null || resendApiKey.isBlank()) {
      throw new IllegalStateException("Resend is enabled but RESEND_API_KEY is missing");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(resendApiKey);

    Map<String, Object> payload = Map.of(
        "from", resendFromEmail,
        "to", List.of(to),
        "subject", subject,
        "text", textBody != null ? textBody : "",
        "html", htmlBody != null ? htmlBody : "");

    ResponseEntity<String> response = restTemplate.exchange(
        resendBaseUrl + "/emails",
        HttpMethod.POST,
        new HttpEntity<>(payload, headers),
        String.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException("Failed to send email using Resend");
    }
  }
}