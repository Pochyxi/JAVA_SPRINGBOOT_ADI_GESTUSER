package com.adi.gestuser.controller;

import com.adi.gestuser.dto.PagedResponseDTO;
import com.adi.gestuser.dto.UserDTO;
import com.adi.gestuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController( UserService userService ) {

        this.userService = userService;

        System.out.println("UserController loaded");
    }

    /*
     * GET USER BY ID
         * Questo metodo permette di ottenere i dati di un utente specificando l'id
         * PREAUTHORIZE:
            * Utente con permesso USER_READ e potere più alto sull'utente specificato
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize( "hasRole('USER')" )
    public ResponseEntity<UserDTO> getUserById( @PathVariable("id") Long id ) {

        return new ResponseEntity<>( userService.getUserDTOById( id ), HttpStatus.OK );
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
