package com.adi.gestuser.service;

public interface EmailService {
    void sendMailMessage(String name, String to, String token, String temporaryPassword, String subject);

    void resendMailMessage(String name, String to, String token, String temporaryPassword, String subject);

    void sendRecoveryMessage(String name, String to, String token);

}
