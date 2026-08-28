package org.example.util;

import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSendUtil {
    private static final Logger LOGGER = Logger.getLogger(EmailSendUtil.class.getName());

    private static String smtpHost = "smtp.gmail.com";
    private static int smtpPort = 587;
    private static String fromEmail = "";
    private static String emailPassword = "";
    private static Mailer mailer;
    private static boolean isConfigured = false;

    static {
        try (InputStream input = EmailSendUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                smtpHost = props.getProperty("mail.smtp.host", "smtp.gmail.com");
                smtpPort = Integer.parseInt(props.getProperty("mail.smtp.port", "587"));
                fromEmail = props.getProperty("mail.sender.email", System.getenv("MAIL_SENDER_EMAIL") != null ? System.getenv("MAIL_SENDER_EMAIL") : "");
                emailPassword = props.getProperty("mail.sender.password", System.getenv("MAIL_SENDER_PASSWORD") != null ? System.getenv("MAIL_SENDER_PASSWORD") : "");
                if (fromEmail != null && !fromEmail.isEmpty() && emailPassword != null && !emailPassword.isEmpty()) {
                    isConfigured = true;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not initialize email configuration: " + e.getMessage());
        }
    }

    private static synchronized Mailer getMailer() {
        if (mailer == null && isConfigured) {
            try {
                mailer = MailerBuilder
                        .withSMTPServer(smtpHost, smtpPort, fromEmail, emailPassword)
                        .withTransportStrategy(smtpPort == 465 ? TransportStrategy.SMTPS : TransportStrategy.SMTP_TLS)
                        .buildMailer();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not build mailer: " + e.getMessage());
            }
        }
        return mailer;
    }

    private static boolean validateEmailParams(String to, String subject, String text) {
        if (!isConfigured) {
            LOGGER.info("Email dispatch skipped: mail credentials not configured in application.properties.");
            return false;
        }
        if (to == null || to.trim().isEmpty() || !to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            LOGGER.warning("Invalid or empty recipient email address: " + to);
            return false;
        }
        if (subject == null || subject.trim().isEmpty() || text == null || text.trim().isEmpty()) {
            LOGGER.warning("Email subject or body cannot be empty");
            return false;
        }
        return true;
    }

    public static void create(String to, String subject, String text, String filePath) {
        try {
            if (!validateEmailParams(to, subject, text)) {
                return;
            }

            Mailer m = getMailer();
            if (m == null) return;

            EmailPopulatingBuilder builder = EmailBuilder.startingBlank()
                    .from(fromEmail)
                    .to(to)
                    .withSubject(subject)
                    .withPlainText(text);

            if (filePath != null && !filePath.trim().isEmpty()) {
                builder.withAttachment("Invoice.pdf", new FileDataSource(filePath));
            }

            Email email = builder.buildEmail();
            m.sendMail(email);
            LOGGER.info("Invoice email sent successfully to: " + to);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not send invoice email: " + e.getMessage());
        }
    }

    public static void create(String to, String subject, String text) {
        create(to, subject, text, null);
    }
}
