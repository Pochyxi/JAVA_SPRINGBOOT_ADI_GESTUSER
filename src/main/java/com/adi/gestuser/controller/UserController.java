package com.adi.gestuser.controller;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.repository.ProfilePermissionRepository;
import com.adi.gestuser.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController( UserService userService, ProfilePermissionRepository profilePermissionRepository ) {

        this.userService = userService;
    }


    /*
     * GET USER BY ID
     * Questo metodo permette di ottenere i dati di un utente specificando l'id
     * PREAUTHORIZE:
     * Utente con permesso USER_READ e potere più alto sull'utente specificato
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<UserDTO> getUserById( @PathVariable("id") Long id ) {

        return new ResponseEntity<>( userService.getUserDTOById( id ), HttpStatus.OK );
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
        userService.createUser( signupDTO, true );

        return new ResponseEntity<>(HttpStatus.CREATED );
    }


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
     * GET ALL USERS
        * Questo metodo permette di ottenere tutti gli utenti
        * PREAUTHORIZE:
            * Utente con permesso USER_READ e potere più alto degli utenti richiesti
     */
    @GetMapping(value = "/all")
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir
    ) {

        return new ResponseEntity<>( userService.getAllUsers( pageNo, pageSize, sortBy, sortDir ), HttpStatus.OK );
    }


    /**
     * MODIFICA UTENTE
        * Questo metodo permette di modificare i dati di un utente specificando l'id
     * PREAUTHORIZE:
        * Utente con permesso USER_UPDATE e potere più alto sull'utente specificato
     */
    @PutMapping("/update/{id}")
    @PreAuthorize( "hasRole('WRITE')" )
    public ResponseEntity<UserDTO> modifyUser( @PathVariable("id") Long id,
                                               @RequestBody UserDTO userDTO ) {

        return new ResponseEntity<>( userService.modifyUser( id, userDTO ), HttpStatus.OK );
    }


    /**
     * ELIMINA UTENTE
        * Questo metodo permette di eliminare un utente specificando l'id
     * PREAUTHORIZE:
        * Utente con permesso USER_DELETE e potere più alto sull'utente specificato
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize( "hasRole('WRITE')" )
    public ResponseEntity<Void> deleteUser( @PathVariable("id") Long id ) {
        userService.deleteUser( id );

        return new ResponseEntity<>( HttpStatus.OK );
    }

    @GetMapping("/email_contains/{email}")
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<PagedResponseDTO<UserDTO>> getByEmailContains(
            @PathVariable("email") String email,
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir
    ) {

        return new ResponseEntity<>( userService.getByEmailContains( email,pageNo, pageSize, sortBy, sortDir), HttpStatus.OK );
    }


    @GetMapping("/profile/{userId}")
    @PreAuthorize( "hasRole('READ')" )
    public ResponseEntity<ProfileDTO> getProfileByUserId( @PathVariable("userId") Long userId ) {

        return new ResponseEntity<>( userService.getProfileByUserId( userId ), HttpStatus.OK );
    }
}
