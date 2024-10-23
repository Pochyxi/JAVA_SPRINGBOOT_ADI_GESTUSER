package com.adi.gestuser.controller;

import com.adi.gestuser.dto.ChangePasswordDTO;
import com.adi.gestuser.dto.ProfileDTO;
import com.adi.gestuser.dto.ProfilePermissionDTO;
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
public class UserManagementController {

    private final UserService userService;
    private final AuthenticationService aut;


    /**
     * CREATE USER
     * @param profileId id del profilo
     * @return ResponseEntity<UserDTO>
     */
    @GetMapping(value = "/profile_permissions/{profileId}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Set<ProfilePermissionDTO>> findByProfileId( @PathVariable("profileId") Long profileId) {
        return new ResponseEntity<>( userService.findByProfileIdDTO( profileId ), HttpStatus.OK );
    }


    /**
     * GET PROFILE BY USER ID
     * @param userId id dell'utente
     * @return ResponseEntity<ProfileDTO>
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<ProfileDTO> getProfileByUserId( @PathVariable("userId") Long userId ) {

        return new ResponseEntity<>( userService.getProfileByUserId( userId ), HttpStatus.OK );
    }


    /**
     * RECOVERY PASSWORD
     * @param email email dell'utente
     * @return ResponseEntity<Void>
     */
    @GetMapping(value = "/recovery_password")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Void> recovery(@RequestParam("email") String email) {
        aut.resetPasswordRequest(email);

        return new ResponseEntity<> (HttpStatus.OK);
    }


    /**
     * CONFIRM TOKEN
     * @param token il token da verificare
     * @param tokentype il tipo di token
     * @return ResponseEntity<Void>
     */
    @GetMapping(value = "/verify")
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token,
                                        @RequestParam("tokentype")String tokentype) {
        aut.verifyToken( token, TokenType.valueOf(tokentype));

        return new ResponseEntity<>( HttpStatus.OK );
    }


    /**
     * CHANGE PASSWORD
     * @param changePasswordDTO dati cambio password
     * @param token token
     * @return Void
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
     * @param userId id dell'utente
     * @param email email da cambiare
     * @return Void
     */
    @PutMapping(value = "/change_email")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Void> changeEmail(@RequestParam Long userId,
                                            @RequestParam String email){
        aut.changeEmail( userId, email );

        return new ResponseEntity<>( HttpStatus.OK );
    }


    /**
     * RESEND VERIFICATION
     * @param userId id dell'utente
     * @return Void
     */
    @PutMapping(value = "/resend_verification")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Void> resendVerification(@RequestParam Long userId) {
        aut.resendVerificationRequest(userId);
        return new ResponseEntity<> (HttpStatus.OK);
    }
}
