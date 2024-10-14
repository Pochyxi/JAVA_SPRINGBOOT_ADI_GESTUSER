package com.adi.gestuser.controller;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.exception.ResourceNotFoundException;
import com.adi.gestuser.repository.ProfilePermissionRepository;
import com.adi.gestuser.repository.UserRepository;
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

    private final UserRepository userRepository;


    @Autowired
    public UserController( UserService userService, UserRepository userRepository, ProfilePermissionRepository profilePermissionRepository ) {

        this.userService = userService;
        this.userRepository = userRepository;
    }

    /*
     * GET USER BY ID
         * Questo metodo permette di ottenere i dati di un utente specificando l'id
         * PREAUTHORIZE:
            * Utente con permesso USER_READ e potere più alto sull'utente specificato
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize( "hasRole('USER')" )
    public ResponseEntity<User> getUserById( @PathVariable("id") Long id ) {

        User user = userRepository.findById( id ).orElseThrow( () -> new ResourceNotFoundException( "User not found with id: " + id ) );

        return new ResponseEntity<>( user, HttpStatus.OK );
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupDTO signupDTO ) {
        userService.createUser( signupDTO, true );

        return new ResponseEntity<>(HttpStatus.CREATED );
    }

    @GetMapping(value = "/username_email/{username_email}")
    public ResponseEntity<UserDTOInternal> findByUsernameOrEmail( @PathVariable("username_email") String username_email) {
        UserDTOInternal user = userService.findDTOByUsernameOrEmail( username_email, username_email );


        return new ResponseEntity<>( user, HttpStatus.OK );

    }

    @GetMapping(value = "/username_email/exist/{username_email}")
    public ResponseEntity<Boolean> existsByUsernameOrEmail( @PathVariable("username_email") String username_email) {
        return new ResponseEntity<>( userService.existsByUsernameOrEmail( username_email, username_email ), HttpStatus.OK );
    }

    @GetMapping(value = "/profile_permissions/{profileId}")
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
    @PreAuthorize( "hasRole('USER')" )
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
    @PreAuthorize( "hasRole('ADMIN')" )
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
    @PreAuthorize( "hasRole('ADMIN')" )
    public ResponseEntity<Void> deleteUser( @PathVariable("id") Long id ) {
        userService.deleteUser( id );

        return new ResponseEntity<>( HttpStatus.OK );
    }

    @GetMapping("/email_contains/{email}")
    @PreAuthorize( "hasRole('USER')" )
    public ResponseEntity<PagedResponseDTO<UserDTO>> getByEmailContains(
            @PathVariable("email") String email,
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir
    ) {

        return new ResponseEntity<>( userService.getByEmailContains( email,pageNo, pageSize, sortBy, sortDir), HttpStatus.OK );
    }

}
