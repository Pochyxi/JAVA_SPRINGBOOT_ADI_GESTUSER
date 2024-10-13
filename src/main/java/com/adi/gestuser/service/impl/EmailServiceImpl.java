package com.adi.gestuser.service.impl;

import com.adi.gestuser.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;



    /* SEND MAIL MESSAGE
        * Questo metodo gestisce l'invio di una mail di verifica all'utente.
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

            // Invia il messaggio di posta elettronica.
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }

    /* SEND RECOVERY MESSAGE
        * Questo metodo gestisce l'invio di una mail di recupero password all'utente.
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
            e.printStackTrace();
        }
    }


    /* GET HTML RECOVERY
        * Questo metodo restituisce il codice HTML per la mail di recupero password.
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


    /* GET HTML VERIFY
        * Questo metodo restituisce il codice HTML per la mail di verifica.
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
