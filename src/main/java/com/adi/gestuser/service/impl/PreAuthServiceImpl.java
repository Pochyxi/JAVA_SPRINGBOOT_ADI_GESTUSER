package com.adi.gestuser.service.impl;

import com.adi.gestuser.entity.User;
import com.adi.gestuser.service.PreAuthService;
import com.adi.gestuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service("authorityService")
@RequiredArgsConstructor
public class PreAuthServiceImpl implements PreAuthService {

    private final UserService userService;



    /* HAS POWER ON SUBJECT
        * Questo metodo controlla se l'utente autenticato ha il permesso di eseguire un'azione su un determinato soggetto.
        * Il soggetto è identificato dall'id.
        * Il permesso è identificato dal nome.
     */
    @Override
    public boolean userHasPowerOnSubject(Long subjectId, String permissionName ) {

        // Controlla se l'utente autenticato ha il permesso di eseguire un'azione su un determinato soggetto.
        boolean hasPowerOnSubject = false;

        // Ottieni l'utente soggetto.
        User userSubject = userService.findById(subjectId);

        // Ottieni il potere dell'utente soggetto.
        int powerOfSubject = userSubject.getProfile().getPower();

        // Ottieni l'utente autenticato.
        User user = userService.getUserByAuthentication();

        // Ottieni il potere dell'utente autenticato.
        int powerOfUser = user.getProfile().getPower();

        // Un utente può eliminare solo se stesso se ha il permesso user_delete 
        if (!permissionName.equals("USER_DELETE")) {
            if (Objects.equals(userSubject.getId(), user.getId())) return true;
        }

        // Ottieni l'autorizzazione dell'utente autenticato.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ottieni i permessi dell'utente autenticato.
        Set<String> authoritiesAsStrings = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Controlla se l'utente autenticato ha il permesso di eseguire un'azione su un determinato soggetto.
        boolean hasPermission = authoritiesAsStrings
                .stream()
                .anyMatch((permission) -> permission.equals(permissionName));

        // Se l'utente autenticato ha il permesso di eseguire un'azione su un determinato soggetto, allora controlla se ha il potere di eseguire l'azione.
        if (hasPermission) {
            if( powerOfUser <= powerOfSubject ) {
                hasPowerOnSubject = true;
            }
        }

        // Restituisce il risultato del controllo.
        return hasPowerOnSubject;
    }


}
