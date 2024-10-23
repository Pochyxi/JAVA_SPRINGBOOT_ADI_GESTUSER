package com.adi.gestuser.controller;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.service.AuthenticationService;
import com.adi.gestuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService aut;


    /*
     * GET USER BY ID
     * Questo metodo permette di ottenere i dati di un utente specificando l'id
     * PREAUTHORIZE:
     * Utente con permesso USER_READ e potere più alto sull'utente specificato
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<UserDTO> getUserById( @PathVariable("id") Long id ) {

        return new ResponseEntity<>( userService.getUserDTOById( id ), HttpStatus.OK );
    }


    /**
     * GET USER BY EMAIL
     * @param email email dell'utente
     * @param pageNo numero pagina
     * @param pageSize dimensione pagina
     * @param sortBy campo di ordinamento
     * @param sortDir ascendente o discendente
     * @return lista utenti paginata
     */
    @GetMapping("/email_contains/{email}")
    @PreAuthorize("hasRole('ROLE_READ')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getByEmailContains(
            @PathVariable("email") String email,
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir
    ) {

        return new ResponseEntity<>( userService.getByEmailContains( email,pageNo, pageSize, sortBy, sortDir), HttpStatus.OK );
    }


    /**
     * GET USER BY EMAIL
     * @param email email dell'utente
     * @return utente
     */
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
     * GET ALL USERS
        * Questo metodo permette di ottenere tutti gli utenti
        * PREAUTHORIZE:
            * Utente con permesso USER_READ e potere più alto degli utenti richiesti
     */
    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ROLE_READ', 'ROLE_WRITE')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir,
            @RequestParam(value = "powerOfUser") Integer powerOfUser
    ) {

        return new ResponseEntity<>( userService.getAllUsers( pageNo, pageSize, sortBy, sortDir, powerOfUser ), HttpStatus.OK );
    }



    /**
     * CREA UTENTE
     * @param signupDTO dati utente
     * @return utente creato
     */
    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyRole('ROLE_READ', 'ROLE_WRITE')")
    public ResponseEntity<UserDTO> createUser( @RequestBody SignupDTO signupDTO ) {
        User user = aut.createUser( signupDTO, true );
        UserDTO userDTO = userService.mapUserToDTO( user );

        return new ResponseEntity<>( userDTO, HttpStatus.OK );
    }


    /**
     * SIGNUP
     * @param signupDTO dati utente
     * @return utente creato
     */
    @PostMapping(value = "/signup")
    @PreAuthorize( "hasRole('ROLE_WRITE')" )
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupDTO signupDTO ) {
        aut.createUser( signupDTO, true );

        return new ResponseEntity<>(HttpStatus.CREATED );
    }


    /**
     * MODIFICA UTENTE
     * @param id id utente
     * @param userDTO dati utente
     * @return utente modificato
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_WRITE')")
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
    @PreAuthorize( "hasRole('ROLE_WRITE')" )
    public ResponseEntity<Void> deleteUser( @PathVariable("id") Long id ) {
        userService.deleteUser( id );

        return new ResponseEntity<>( HttpStatus.OK );
    }
}
