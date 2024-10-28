package org.example.util;

import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSendUtil {
    private static final Logger LOGGER = Logger.getLogger(EmailSendUtil.class.getName());

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 465;
    private static final String FROM_EMAIL = "sadeepanithushika@gmail.com";
    private static final String EMAIL_PASSWORD = "ybrk mzdz povo bmdq";

    private static Mailer mailer;

    private static Mailer getMailer() {
        if (mailer == null) {
            mailer = MailerBuilder
                    .withSMTPServer(SMTP_HOST, SMTP_PORT, FROM_EMAIL, EMAIL_PASSWORD)
                    .withTransportStrategy(TransportStrategy.SMTPS)
                    .buildMailer();
        }
        return mailer;
    }

    private static boolean validateEmailParams(String to, String subject, String text) {
        if (to == null || to.trim().isEmpty()) {
            LOGGER.severe("Recipient email address cannot be null or empty");
            return false;
        }
        if (!to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            LOGGER.severe("Invalid recipient email address format");
            return false;
        }
        if (subject == null || subject.trim().isEmpty()) {
            LOGGER.severe("Email subject cannot be null or empty");
            return false;
        }
        if (text == null || text.trim().isEmpty()) {
            LOGGER.severe("Email content cannot be null or empty");
            return false;
        }
        return true;
    }

    public static void create(String to, String subject, String text, String file) {
        try {
            if (!validateEmailParams(to, subject, text)) {
                return;
            }

            if (file == null || file.trim().isEmpty()) {
                LOGGER.severe("File path cannot be null or empty");
                return;
            }

            Email email = EmailBuilder.startingBlank()
                    .from(FROM_EMAIL)
                    .to(to)
                    .withSubject(subject)
                    .withPlainText(text)
                    .withAttachment("Bill.pdf", new FileDataSource(file))
                    .buildEmail();

            getMailer().sendMail(email);
            LOGGER.info("Email with attachment sent successfully to: " + to);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending email with attachment: " + e.getMessage(), e);
        }
    }

    public static void create(String to, String subject, String text) {
        try {
            if (!validateEmailParams(to, subject, text)) {
                return;
            }

            Email email = EmailBuilder.startingBlank()
                    .from(FROM_EMAIL)
                    .to(to)
                    .withSubject(subject)
                    .withPlainText(text)
                    .buildEmail();

            getMailer().sendMail(email);
            LOGGER.info("Email sent successfully to: " + to);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending email: " + e.getMessage(), e);
        }
    }
}