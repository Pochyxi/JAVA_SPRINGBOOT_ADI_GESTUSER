package com.adi.gestuser.controller;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.enums.TokenType;
import com.adi.gestuser.service.AuthenticationService;
import com.adi.gestuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;
    private final AuthenticationService aut;


    @GetMapping(value = "/create")
    @PreAuthorize("hasAnyRole('ROLE_READ', 'ROLE_WRITE')")
    public ResponseEntity<UserDTO> createUser( @RequestBody SignupDTO signupDTO ) {
        User user = aut.createUser( signupDTO, true );
        UserDTO userDTO = userService.mapUserToDTO( user );

        return new ResponseEntity<>( userDTO, HttpStatus.OK );
    }

    @GetMapping(value = "/findByEmail/{email}")
    @PreAuthorize("hasAnyRole('ROLE_READ', 'ROLE_WRITE')")
    public ResponseEntity<UserDTO> findByEmail( @PathVariable("email") String email) {
        Optional<User> user = userService.findByEmail( email );

        return user.map( value -> new ResponseEntity<>( userService.mapUserToDTO( value ), HttpStatus.OK ) )
                .orElseGet( () -> new ResponseEntity<>( HttpStatus.NOT_FOUND ) );

    }


    /*
     * GET USER BY AUTHENTICATION
     * Questo metodo permette di ottenere i dati di un utente per username o email
     * PREAUTHORIZE:
     * Utente con permesso READ
     */
    @GetMapping(value = "/username_email/{username_email}")
    @PreAuthorize("hasAnyRole('ROLE_READ', 'ROLE_WRITE')")
    public ResponseEntity<UserDTOInternal> findByUsernameOrEmail( @PathVariable("username_email") String username_email) {
        UserDTOInternal user = userService.findDTOByUsernameOrEmail( username_email, username_email );


        return new ResponseEntity<>( user, HttpStatus.OK );

    }

    /*
     * LOGIN
     * Questo metodo permette di effettuare la registrazione
     * PREAUTHORIZATION
     * NONE
     */
    @PostMapping(value = "/signup")
    @PreAuthorize( "hasRole('ROLE_WRITE')" )
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupDTO signupDTO ) {
        aut.createUser( signupDTO, true );

        return new ResponseEntity<>(HttpStatus.CREATED );
    }


    /*
     * GET USER BY AUTHENTICATION
     * Questo metodo permette di ottenere un booleano in base all'esistenza di un utente
     *  con username o email specificati
     * PREAUTHORIZE:
     * Utente con permesso READ
     */
    @GetMapping(value = "/username_email/exist/{username_email}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Boolean> existsByUsernameOrEmail( @PathVariable("username_email") String username_email) {
        return new ResponseEntity<>( userService.existsByUsernameOrEmail( username_email, username_email ), HttpStatus.OK );
    }


    /*
     * GET USER BY AUTHENTICATION
     * Questo metodo permette di ottenere i profiles permissions di un utente
     * PREAUTHORIZE:
     * Utente con permesso READ
     */
    @GetMapping(value = "/profile_permissions/{profileId}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Set<ProfilePermissionDTO>> findByProfileId( @PathVariable("profileId") Long profileId) {
        return new ResponseEntity<>( userService.findByProfileIdDTO( profileId ), HttpStatus.OK );
    }

    /*
     * GET USER BY AUTHENTICATION
     * Questo metodo permette di ottenere i dati profilo di un utente per id
     * PREAUTHORIZE:
     * Utente con permesso READ
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<ProfileDTO> getProfileByUserId( @PathVariable("userId") Long userId ) {

        return new ResponseEntity<>( userService.getProfileByUserId( userId ), HttpStatus.OK );
    }


    @GetMapping(value = "/verify")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token,
                                        @RequestParam("tokentype")String tokentype) {
        aut.verifyToken( token, TokenType.valueOf(tokentype));

        return new ResponseEntity<>( HttpStatus.OK );
    }

    /**
     * CHANGE PASSWORD
     * Questo metodo permette di modificare la password
     * PREAUTHORIZATION:
     * 1 - E' possibile cambiare la password avendo a disposizione il token inviato tramite email
     * 2 - Attraverso autenticazione specificando username e vecchia password
     */
    @PutMapping(value = "/change_password")
    @PreAuthorize( "hasRole('WRITE')" )
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
                                               @RequestParam(value = "token", required = false) String token ) {
        aut.changePassword( changePasswordDTO, token );

        return new ResponseEntity<>( HttpStatus.OK );
    }

    /**
     * CHANGE EMAIL
     * Questo metodo permette di modificare l'email
     * PREAUTHORIZATION:
     * Utente con permesso USER_UPDATE
     */
    @PutMapping(value = "/change_email")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Void> changeEmail(@RequestParam Long userId,
                                            @RequestParam String email){
        aut.changeEmail( userId, email );

        return new ResponseEntity<>( HttpStatus.OK );
    }


    /**
     * RECOVERY PASSWORD
     * Questo metodo permette di recuperare la password
     * PREAUTHORIZATION:
     * NONE
     */
    @GetMapping(value = "/recovery_password")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Void> recovery(@RequestParam("email") String email) {
        aut.resetPasswordRequest(email);

        return new ResponseEntity<> (HttpStatus.OK);
    }


    /**
     * RESEND VERIFICATION
     * Questo metodo permette di inviare nuovamente la mail di verifica
     * PREAUTHORIZATION:
     * Utente con permesso WRITE
     */
    @PutMapping(value = "/resend_verification")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Void> resendVerification(@RequestParam Long userId) {
        aut.resendVerificationRequest(userId);
        return new ResponseEntity<> (HttpStatus.OK);
    }
}
