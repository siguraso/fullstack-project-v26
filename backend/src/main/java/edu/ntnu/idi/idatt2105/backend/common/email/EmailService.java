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

/**
 * Service responsible for sending transactional emails using the Resend API.
 * <p>
 * The service can be toggled on or off via configuration properties and
 * supports both plain text and HTML email bodies.
 */
@Slf4j
@Service
public class EmailService {
  private final RestTemplate restTemplate = new RestTemplate();
  private final boolean resendEnabled;
  private final String resendApiKey;
  private final String resendFromEmail;
  private final String resendBaseUrl;

  /**
   * Creates a new {@code EmailService} configured from Spring properties.
   *
   * @param resendEnabled  whether integration with Resend is enabled
   * @param resendApiKey   API key used to authenticate with Resend
   * @param resendFromEmail the default sender address used for outgoing emails
   * @param resendBaseUrl  base URL of the Resend API
   */
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

  /**
   * Sends a plain text email using the configured Resend integration.
   *
   * @param to      recipient email address
   * @param subject subject of the email
   * @param body    plain text body of the email
   */
  public void sendEmail(String to, String subject, String body) {
    sendResendEmail(to, subject, body, null);
  }

  /**
   * Sends an HTML email using the configured Resend integration.
   *
   * @param to       recipient email address
   * @param subject  subject of the email
   * @param htmlBody HTML-formatted body of the email
   */
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