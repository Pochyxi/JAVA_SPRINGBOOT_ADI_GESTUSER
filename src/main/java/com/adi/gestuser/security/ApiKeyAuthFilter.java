package com.adi.gestuser.security;

import com.adi.gestuser.entity.ApiKey;
import com.adi.gestuser.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER_NAME = "X-API-KEY";
    private final ApiKeyRepository apiKeyRepository;


    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain ) throws ServletException, IOException {

        String apiKeyValue = request.getHeader( API_KEY_HEADER_NAME );

        if( apiKeyValue != null && !apiKeyValue.isEmpty() ) {
            ApiKey apiKey = apiKeyRepository.findByApikey( apiKeyValue );

            if( apiKey != null ) {
                // Verifica se l'API key è scaduta
                if( apiKey.getExpireDate().isAfter( LocalDateTime.now() ) ) {
                    // Crea le autorità basate sul ruolo
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add( new SimpleGrantedAuthority( "ROLE_" + apiKey.getApikeyRole().name() ) );

                    // Crea l'oggetto Authentication
                    Authentication authentication = new ApiKeyAuthenticationToken(
                            apiKeyValue,
                            null,
                            authorities,
                            apiKey
                    );

                    // Imposta l'autenticazione nel contesto di sicurezza
                    SecurityContextHolder.getContext().setAuthentication( authentication );

                    // Procedi con la catena dei filtri
                    filterChain.doFilter( request, response );
                } else {
                    // API key scaduta
                    response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                    response.getWriter().write( "API key scaduta." );
                }
            } else {
                // API key non valida
                response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                response.getWriter().write( "API key non valida." );
            }
        } else {
            // Header API key mancante
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
            response.getWriter().write( "Header API key mancante." );
        }
    }
}

