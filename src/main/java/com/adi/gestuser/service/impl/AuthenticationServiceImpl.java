package com.adi.gestuser.service.impl;

import com.adi.gestuser.dto.ChangePasswordDTO;
import com.adi.gestuser.dto.JwtAuthResponseDTO;
import com.adi.gestuser.dto.LoginDTO;
import com.adi.gestuser.dto.SignupDTO;
import com.adi.gestuser.entity.Confirmation;
import com.adi.gestuser.entity.Profile;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.enums.ProfileList;
import com.adi.gestuser.enums.TokenType;
import com.adi.gestuser.exception.ErrorCodeList;
import com.adi.gestuser.exception.ResourceNotFoundException;
import com.adi.gestuser.exception.appException;
import com.adi.gestuser.repository.ConfirmationRepository;
import com.adi.gestuser.repository.ProfileRepository;
import com.adi.gestuser.security.JwtTokenProvider;
import com.adi.gestuser.service.AuthenticationService;
import com.adi.gestuser.service.EmailService;
import com.adi.gestuser.service.UserService;
import com.adi.gestuser.utils.FirstPasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final ProfileRepository profileRepository;

    private final ConfirmationRepository confirmationRepository;

    private final EmailService emailService;


    /* LOGIN
        * Questo metodo gestisce il processo di autenticazione di un utente.
     */
    @Override
    public JwtAuthResponseDTO login( LoginDTO loginDTO) {

        // Controlla se l'username o l'email forniti esistono nel database.
        if (!userService.existsByUsernameOrEmail(loginDTO.getUsernameOrEmail(), loginDTO.getUsernameOrEmail())) {
            throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.BADCREDENTIALS);
        }

        // Autentica l'utente utilizzando l'oggetto AuthenticationManager.
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO
                        .getUsernameOrEmail(), loginDTO.getPassword()));

        // Imposta l'oggetto Authentication nel SecurityContext.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Recupera l'utente dal database
        User user = userService.findByUsernameOrEmail(loginDTO.getUsernameOrEmail(), loginDTO.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodeList.NF404
                ));

        // Crea un nuovo oggetto JwtAuthResponseDTO e popola i suoi campi con il token di accesso e l'utente.
        JwtAuthResponseDTO jwtAuthResponseDTO = new JwtAuthResponseDTO();
        jwtAuthResponseDTO.setAccessToken(jwtTokenProvider.generateToken(authentication));
        jwtAuthResponseDTO.setUser( userService.mapUserToDTO( user ) );


        // Restituisce l'oggetto JwtAuthResponseDTO.
        return jwtAuthResponseDTO;
    }

    /* SIGNUP
     * Questo metodo gestisce il processo di registrazione di un utente.
     */
    @Override
    @Transactional
    public User createUser( SignupDTO signupDTO, boolean confEmail) {

        // Controlla se l'username o l'email forniti esistono nel database.
        if (userService.existsByUsername(signupDTO.getUsername())) {
            throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGUSERNAME);
        }

        if (userService.existsByEmail(signupDTO.getEmail())) {
            throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGEMAIL);
        }


        // Crea un nuovo oggetto User e popola i suoi campi con i valori forniti.
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setEnabled(false);
        String temporaryPassword = FirstPasswordGenerator.generatePass();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setTemporaryPassword(true);

        // Salva l'utente nel database.
        user = userService.save(user);

        // Crea un nuovo oggetto Confirmation e popola i suoi campi con l'utente.
        Confirmation confirmation = new Confirmation(user);
        confirmation.setTokenType( TokenType.email);
        // Salva la conferma nel database.
        confirmationRepository.save(confirmation);

        // todo: eliminare in produzione
        // Invia un'email all'utente con il token di conferma e la password temporanea.
        // condizione creata ai fini della generazione automatica degli utenti
        if (confEmail) {

            emailService.sendMailMessage(
                    user.getUsername(),
                    user.getEmail(),
                    confirmation.getToken(),
                    temporaryPassword,
                    "Richiesta di verifica Account e password temporanea"
            );
        } else {
            user.setPassword( passwordEncoder.encode( "Admin94!" ) );
        }

        // Crea un nuovo oggetto Profile e popola i suoi campi con l'utente.
        // DEFAULT: DIPENDENTE
        Profile userProfile = new Profile( ProfileList.USER);

        // Salva il profilo nel database.
        userProfile.setUser(user);

        // Salva il profilo nel database.
        profileRepository.save(userProfile);

        return user;
    }

    @Override
    public void changeEmail(Long userId, String email) {
        User user = userService.findById(userId);

        user.setEmail(email);

        String tempPass = FirstPasswordGenerator.generatePass();

        user.setPassword(passwordEncoder.encode(tempPass));

        user.setTemporaryPassword(true);

        user.setEnabled(false);

        userService.save(user);

        Confirmation confirmation = new Confirmation(user);

        confirmation.setTokenType( TokenType.email);

        confirmationRepository.save(confirmation);

        emailService.sendMailMessage(
                user.getUsername(),
                user.getEmail(),
                confirmation.getToken(),
                tempPass,
                "Richiesta di verifica Account e password temporanea"
        );

    }


    /* CHANGE PASSWORD
        * Questo metodo gestisce il processo di cambio password di un utente.
     */
    public void changePassword( ChangePasswordDTO changePasswordDTO, String token) {
        // User a null
        User user;

        // Se il token è diverso da null, allora l'utente ha richiesto il cambio password tramite email.
        if (token != null) {
            Confirmation confirmation = verifyToken(token, TokenType.password);

            user = confirmation.getUser();

        } else {

            // Altrimenti l'utente ha richiesto il cambio password tramite il proprio account.
            user = userService.findByUsernameOrEmail(changePasswordDTO.getUsernameOrEmail(), changePasswordDTO.getUsernameOrEmail()).orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCodeList.NF404
            ));

            // Controlla se la vecchia password fornita è corretta.
            boolean checkPass = new BCryptPasswordEncoder().matches(
                    changePasswordDTO.getOldPassword(),
                    user.getPassword());

            // Se la vecchia password fornita non è corretta, allora lancia un'eccezione.
            if (!checkPass) throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.BADCREDENTIALS);

            // Controlla se la nuova password fornita è uguale alla vecchia password.
            if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
                throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.BADCREDENTIALS);
            }
        }


        // Se user è null, allora lancia un'eccezione.
        if (user == null) throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.BADCREDENTIALS);


        // Imposta la nuova password e la data di verifica del token. Tutti i vecchi JWT non saranno più validi.
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setTemporaryPassword(false);
        user.setDateTokenCheck(LocalDateTime.now());

        // Salva l'utente nel database.
        userService.createUser(user);
    }

    /* VERIFY TOKEN
        * Questo metodo gestisce il processo di verifica del token di conferma.
     */
    @Override
    public Confirmation verifyToken( String token, TokenType tokenType) {

        // Recupera la conferma dal database.
        Confirmation confirmation = confirmationRepository.findByToken(token);

        if(!confirmation.getTokenType().equals(tokenType)){
            throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_TOKEN);
        }

        // Se la conferma è null, allora lancia un'eccezione.
        User user = userService.findByEmail(confirmation.getUser().getEmail()).orElseThrow(
                () -> new appException(
                        HttpStatus.BAD_REQUEST,
                        ErrorCodeList.NF404
                )
        );

        // Imposta l'utente come abilitato.
        user.setEnabled(true);

        // Salva l'utente nel database.
        userService.save(user);

        Set<Confirmation> confirmationSet  = confirmationRepository.findByTokenTypeAndUserId(tokenType, user.getId());

        // Elimina la conferma dal database.
        confirmationRepository.deleteAll( confirmationSet );


        // Restituisce la conferma.
        return confirmation;
    }


    /* RESET PASSWORD REQUEST
        * Questo metodo gestisce il processo di richiesta di reset della password.
     */
    @Override
    public void resetPasswordRequest(String email) {


        // Controlla se l'email fornita esiste nel database.
        if (userService.existsByEmail(email)) {
            User user = userService.findByEmail(email).orElseThrow(() ->
                    new ResourceNotFoundException( ErrorCodeList.NF404)
            );


            // Crea un nuovo oggetto Confirmation e popola i suoi campi con l'utente.
            Confirmation confirmation = new Confirmation(user);
            confirmation.setUser(user);
            confirmation.setTokenType( TokenType.password);

            // Salva la conferma nel database.
            confirmationRepository.save(confirmation);

            // Invia un'email all'utente con il token di conferma.
            emailService.sendRecoveryMessage(user.getUsername(), user.getEmail(), confirmation.getToken());
        }
    }

    @Override
    public void resendVerificationRequest(Long userId) {

        User user = userService.findById(userId);

        Set<Confirmation> confirmationSet = confirmationRepository.findByUserId(user.getId());

        Confirmation confirmation = confirmationSet.stream().findFirst().orElseGet(() -> {
                    Confirmation newConfirmation = new Confirmation(user);
                    newConfirmation.setTokenType( TokenType.email);
                    // Salva la conferma nel database.
                    confirmationRepository.save(newConfirmation);
                return newConfirmation;
                });


        String temporaryPass = FirstPasswordGenerator.generatePass();

        user.setPassword(passwordEncoder.encode(temporaryPass));

        user.setEnabled(false);

        user.setTemporaryPassword(false);

        userService.save(user);

        emailService.resendMailMessage(
                user.getUsername(),
                user.getEmail(),
                confirmation.getToken(),
                temporaryPass,
                "Le informazioni del tuo account sono state aggiornate. Verifica il tuo account."
        );

    }

}
