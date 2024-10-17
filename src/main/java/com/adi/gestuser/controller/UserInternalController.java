package com.adi.gestuser.controller;

import com.adi.gestuser.dto.ProfileDTO;
import com.adi.gestuser.dto.ProfilePermissionDTO;
import com.adi.gestuser.dto.SignupDTO;
import com.adi.gestuser.dto.UserDTOInternal;
import com.adi.gestuser.enums.TokenType;
import com.adi.gestuser.service.AuthenticationService;
import com.adi.gestuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;
    private final AuthenticationService aut;


    /*
     * GET USER BY AUTHENTICATION
     * Questo metodo permette di ottenere i dati di un utente per username o email
     * PREAUTHORIZE:
     * Utente con permesso READ
     */
    @GetMapping(value = "/username_email/{username_email}")
    @PreAuthorize( "hasRole('READ')" )
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
    @PreAuthorize( "hasRole('WRITE')" )
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
    @PreAuthorize( "hasRole('READ')" )
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
    @PreAuthorize( "hasRole('READ')" )
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
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<ProfileDTO> getProfileByUserId( @PathVariable("userId") Long userId ) {

        return new ResponseEntity<>( userService.getProfileByUserId( userId ), HttpStatus.OK );
    }


    @GetMapping(value = "/verify")
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<Void> confirm(@RequestParam("token") String token,
                                        @RequestParam("tokentype")String tokentype) {
        aut.verifyToken( token, TokenType.valueOf(tokentype));

        return new ResponseEntity<>( HttpStatus.OK );
    }
}
