package com.adi.gestuser.service.impl;

import com.adi.gestuser.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger( EmailServiceImpl.class);

    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;


    /**
     * SEND MAIL MESSAGE
     * @param name nome dell'utente
     * @param to email dell'utente
     * @param token token di verifica
     * @param temporaryPassword password temporanea
     * @param subject oggetto della mail
     */
    @Override
    @Async
    public void sendMailMessage(String name, String to, String token, String temporaryPassword, String subject) {

        try {

            // Crea un nuovo messaggio di posta elettronica.
            MimeMessage message = javaMailSender.createMimeMessage();
            // Crea un nuovo helper per il messaggio di posta elettronica.
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // Ottieni il codice HTML per la mail di verifica.
            String html = getHtmlVerify(name, token, temporaryPassword);

            // Imposta il soggetto, il mittente, il destinatario e il testo del messaggio.
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(html, true);

            // Aggiungi l'immagine come risorsa inline.
            ClassPathResource logoImage = new ClassPathResource("static/images/logo.jpg");
            helper.addInline("logoImage", logoImage);

            // Invia il messaggio di posta elettronica.
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error( "Errore nell'invio della mail di verifica: {}", e.getMessage() );
        }
    }

    /**
     * RESEND MAIL MESSAGE
     * @param name nome dell'utente
     * @param to email dell'utente
     * @param token token di verifica
     * @param temporaryPassword password temporanea
     * @param subject oggetto della mail
     */
    @Override
    @Async
    public void resendMailMessage( String name, String to, String token, String temporaryPassword, String subject ) {
        try {

            // Crea un nuovo messaggio di posta elettronica.
            MimeMessage message = javaMailSender.createMimeMessage();
            // Crea un nuovo helper per il messaggio di posta elettronica.
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // Ottieni il codice HTML per la mail di verifica.
            String html = getResendEmailVerify(name, token, temporaryPassword);

            // Imposta il soggetto, il mittente, il destinatario e il testo del messaggio.
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(html, true);

            // Invia il messaggio di posta elettronica.
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error( "Errore nell'invio della mail di verifica ripristino password: {}", e.getMessage() );
        }
    }


    /**
     * SEND RECOVERY MESSAGE
     * @param name nome dell'utente
     * @param to email dell'utente
     * @param token token di verifica
     */
    @Override
    @Async
    public void sendRecoveryMessage(String name, String to, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String html = getHtmlRecovery(name, token);

            helper.setSubject("Richiesta di cambio password");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error( "Errore nell'invio della mail di recupero password: {}", e.getMessage() );
        }
    }


    /**
     * GET HTML RECOVERY
     * @param name nome utente
     * @param token token di verifica
     * @return codice HTML per la mail di recupero password
     */
    private String getHtmlRecovery(String name, String token){
        String verificationUrl = host + "auth/change-password?token=" + token;

        return "<html>" +
                "<body>" +
                "<h1>Ciao " + name + ",</h1>" +
                "<p>Hai richiesto un cambio password</p>" +
                "<br />" +
                "<p>Per favore clicca sul bottone sottostante per cambiare la password</p>" +
                "<br />" +
                "<p><a href='" + verificationUrl + "' " +
                "style='background-color: #00c7db; color: white; padding: 10px 20px; text-decoration: none; " +
                "border-radius: 5px;'>" +
                "Cambia Password</a></p>" +
                "<p>Grazie,<br/>Il team di SmartAxcy</p>" +
                "</body>" +
                "</html>";
    }


    /**
     * GET HTML VERIFY
     * @param name nome utente
     * @param token token di verifica
     * @param temporaryPassword password temporanea
     * @return codice HTML per la mail di verifica
     */
    public String getHtmlVerify(String name, String token, String temporaryPassword) {
        String verificationUrl = host + "auth/email-confirmation?token=" + token;

        return "<html>" +
                "<body>" +
                "<h1>Ciao " + name + ",</h1>" +
                "<p>Benvenuto in SmartAxcy, di seguito la tua Password temporanea: </p>" +
                "<br />" +
                "<p>" + temporaryPassword + "</p>" +
                "<br />" +
                "<img src='cid:logoImage' alt='SmartAxcy Logo' style='width: 200px;'/>" +
                "<p>Al primo Login ti verrà chiesto di cambiare la Password, ma prima abbiamo bisogno che tu verifichi questa email.</p>" +
                "<p>Per favore clicca sul bottone sottostante per verificare il tuo account:</p>" +
                "<br />" +
                "<p><a href='" + verificationUrl + "' " +
                "style='background-color: #00c7db; color: white; padding: 10px 20px; text-decoration: none; " +
                "border-radius: 5px;'>" +
                "Verifica Account</a></p>" +
                "<br />" +
                "<p>Grazie,<br/>Il team di SmartAxcy</p>" +
                "</body>" +
                "</html>";
    }

    /**
     * GET RESEND EMAIL VERIFY
     * @param name nome utente
     * @param token token di verifica
     * @param temporaryPassword password temporanea
     * @return codice HTML per la mail di verifica
     */
    public String getResendEmailVerify(String name, String token, String temporaryPassword) {
        String verificationUrl = host + "auth/email-confirmation?token=" + token;

        return "<html>" +
                "<body>" +
                "<h1>Ciao " + name + ",</h1>" +
                "<p>Ciao! La password del tuo account è stata ripristinata, di seguito le nuove informazioni. </p>" +
                "<br />" +
                "<p>" + temporaryPassword + "</p>" +
                "<br />" +
                "<p>Al primo Login ti verrà chiesto di cambiare la Password, ma prima abbiamo bisogno che tu verifichi questa email.</p>" +
                "<p>Per favore clicca sul bottone sottostante per verificare il tuo account:</p>" +
                "<br />" +
                "<p><a href='" + verificationUrl + "' " +
                "style='background-color: #00c7db; color: white; padding: 10px 20px; text-decoration: none; " +
                "border-radius: 5px;'>" +
                "Verifica Account</a></p>" +
                "<br />" +
                "<p>Grazie,<br/>Il team di SmartAxcy</p>" +
                "</body>" +
                "</html>";
    }


}
