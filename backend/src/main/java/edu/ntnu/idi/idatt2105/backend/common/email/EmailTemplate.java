package edu.ntnu.idi.idatt2105.backend.common.email;

public class EmailTemplate {
  public static String invitationEmail(String orgName, String inviteUrl) {
    return """
        <html>
          <body style=\"font-family: Arial, sans-serif; color: #111827; line-height: 1.5;\">
            <h2 style=\"margin-bottom: 8px;\">You are invited to join %s</h2>
            <p>An admin has invited you to join your team in Regula.</p>
            <p>Use the link below to complete your account setup:</p>
            <p>
              <a href=\"%s\" style=\"display:inline-block;padding:10px 14px;background:#2563eb;color:white;text-decoration:none;border-radius:6px;\">
                Accept invitation
              </a>
            </p>
            <p>If the button does not work, copy and paste this URL into your browser:</p>
            <p><a href=\"%s\">%s</a></p>
            <p style=\"margin-top: 24px; color: #6b7280; font-size: 12px;\">
              If you did not expect this invitation, you can safely ignore this email.
            </p>
          </body>
        </html>
        """.formatted(orgName, inviteUrl, inviteUrl, inviteUrl);
  }
}