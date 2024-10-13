package com.adi.gestuser.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Classe di configurazione per le proprietà di sicurezza dell'applicazione.
 * Mappa le proprietà dal file di configurazione con il prefisso "app.security.users".
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.security.users")
public class SecurityProperties {

    /**
     * Proprietà per l'utente con permessi di lettura.
     */
    private User read;

    /**
     * Proprietà per l'utente con permessi di scrittura.
     */
    private User write;

    /**
     * Classe interna che rappresenta un utente con username e password.
     */
    @Setter
    @Getter
    public static class User {
        /**
         * Nome utente.
         */
        private String username;

        /**
         * Password dell'utente.
         */
        private String password;
    }
}
