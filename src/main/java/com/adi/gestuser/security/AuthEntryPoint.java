package com.adi.gestuser.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Punto di ingresso personalizzato per l'autenticazione.
 * Gestisce le risposte in caso di errori di autenticazione.
 */
@Log
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {

    /**
     * Gestisce la risposta quando l'autenticazione fallisce.
     *
     * @param request La richiesta HTTP in arrivo.
     * @param response La risposta HTTP da inviare.
     * @param authException L'eccezione di autenticazione lanciata.
     * @throws IOException Se si verifica un errore durante la scrittura della risposta.
     */
    @Override
    public void commence( final HttpServletRequest request, final HttpServletResponse response,
                          final AuthenticationException authException) throws IOException {
        String errMsg = "Username e/o Password non corrette!";

        log.warning("Errore Sicurezza: " + authException.getMessage());

        // Impostazione della risposta di errore
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

        PrintWriter writer = response.getWriter();
        writer.println(errMsg);
    }

    /**
     * Configura le proprietà dopo l'inizializzazione del bean.
     */
    @Override
    public void afterPropertiesSet() {
        setRealmName("REAME");
        super.afterPropertiesSet();
    }
}
