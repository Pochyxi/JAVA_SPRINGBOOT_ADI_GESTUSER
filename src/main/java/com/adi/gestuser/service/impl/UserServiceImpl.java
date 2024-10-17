package com.adi.gestuser.service.impl;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.*;
import com.adi.gestuser.enums.PermissionList;
import com.adi.gestuser.enums.ProfileList;
import com.adi.gestuser.enums.TokenType;
import com.adi.gestuser.exception.ErrorCodeList;
import com.adi.gestuser.exception.ResourceNotFoundException;
import com.adi.gestuser.exception.appException;
import com.adi.gestuser.repository.*;
import com.adi.gestuser.service.EmailService;
import com.adi.gestuser.service.UserService;
import com.adi.gestuser.utils.FirstPasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    private final ProfileRepository profileRepository;

    private final ProfilePermissionRepository profilePermissionRepository;

    private final PasswordEncoder passwordEncoder;

    private final ConfirmationRepository confirmationRepository;

    private final EmailService emailService;


    // VOID RETURNS

    /* SIGNUP
     * Questo metodo gestisce il processo di registrazione di un utente.
     */
    @Override
    @Transactional
    public void createUser( SignupDTO signupDTO, boolean confEmail ) {

        // Controlla se l'username o l'email forniti esistono nel database.
        if( existsByUsername( signupDTO.getUsername() ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGUSERNAME );
        }

        if( existsByEmail( signupDTO.getEmail() ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGEMAIL );
        }


        // Crea un nuovo oggetto User e popola i suoi campi con i valori forniti.
        User user = new User();
        user.setUsername( signupDTO.getUsername() );
        user.setEmail( signupDTO.getEmail() );
        user.setEnabled( false );
        String temporaryPassword = FirstPasswordGenerator.generatePass();
        user.setPassword( passwordEncoder.encode( temporaryPassword ) );
        user.setTemporaryPassword( true );

        // Salva l'utente nel database.
        user = save( user );

        // Crea un nuovo oggetto Confirmation e popola i suoi campi con l'utente.
        Confirmation confirmation = new Confirmation( user );
        confirmation.setTokenType( TokenType.email );
        // Salva la conferma nel database.
        confirmationRepository.save( confirmation );

        // todo: eliminare in produzione
        // Invia un'email all'utente con il token di conferma e la password temporanea.
        // condizione creata ai fini della generazione automatica degli utenti
        if( confEmail ) {

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
        Profile userProfile = new Profile( ProfileList.USER );

        // Salva il profilo nel database.
        userProfile.setUser( user );

        // Salva il profilo nel database.
        profileRepository.save( userProfile );

    }

    @Override
    public void createUser( User user ) {
        userRepository.save( user );
    }

    /* DELETE USER
     * Eliminazione di un utente
     */
    @Override
    public void deleteUser( Long id ) {

        User user = getUserById( id );

        userRepository.delete( user );
    }


    // USER RETURNS

    /* SAVE USER
     * Salvataggio di un utente
     */
    @Override
    public User save( User user ) {
        return userRepository.save( user );
    }

    /* FIND BY ID
     * Recupero di un utente in base al suo ID
     */
    @Override
    public User findById( Long id ) {

        return userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( ErrorCodeList.NF404 ) );
    }

    /* FIND BY EMAIL
     * Recupero di un utente in base alla sua email
     */
    @Override
    public Optional<User> findByEmail( String email ) {

        return userRepository.findByEmail( email );
    }

    /* FIND BY USERNAME OR EMAIL
     * Recupero di un utente in base al suo username o alla sua email
     */
    @Override
    public Optional<User> findByUsernameOrEmail( String username, String email ) {
        return userRepository.findByUsernameOrEmail( username, email );
    }

    @Override
    public UserDTOInternal findDTOByUsernameOrEmail( String username, String email ) {
        User user = userRepository.findByUsernameOrEmail( username, email )
                .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.NF404 ) );

        UserDTO userDTO = mapUserToDTO( user );

        return UserDTOInternal.builder()
                .password( user.getPassword() )
                .id( user.getId() )
                .username( user.getUsername() )
                .email( user.getEmail() )
                .isEnabled( user.isEnabled() )
                .isTemporaryPassword( user.isTemporaryPassword() )
                .dateTokenCheck( user.getDateTokenCheck() )
                .profileName( String.valueOf( user.getProfile().getName() ) )
                .profilePermissions( userDTO.getProfilePermissions() )
                .build();
    }


    @Override
    public Boolean existsByUsername( String username ) {

        return userRepository.existsByUsername( username );
    }

    @Override
    public Boolean existsByEmail( String email ) {

        return userRepository.existsByEmail( email );
    }

    @Override
    public Boolean existsByUsernameOrEmail( String username, String email ) {
        return userRepository.existsByUsernameOrEmail( username, email );
    }

    /* GET USER BY AUTHENTICATION
     * Questo metodo recupera l'utente autenticato dal database.
     */
    public User getUserByAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail( email )
                .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.NF404 ) );
    }

    @Override
    public Page<User> findByProfilePowerGreaterThanEqual( int power, Pageable page ) {
        return userRepository.findByProfilePowerGreaterThanEqual( power, page );
    }

    /* DTO MAP TO ENTITY
     * Mapping manuale
     */
    @Override
    public User mapUserDTOToEntity( UserDTO userDTO ) {

        User user = new User();
        user.setId( userDTO.getId() );
        user.setUsername( userDTO.getUsername() );
        user.setEmail( userDTO.getEmail() );

        Profile profile = profileRepository.findByUserId( userDTO.getId() );
        user.setProfile( profile );

        return user;
    }


    // BOOLEAN RETURNS

    /* EXISTING USERNAME
     * Questo metodo controlla se l'username fornito esiste già nel database.
     */
    private void existingUsername( String username ) {
        boolean usernameExists = false;
        if( username != null ) {
            usernameExists = userRepository.existsByUsername( username );
        }
        if( usernameExists ) throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGUSERNAME );
    }


    /* EXISTING EMAIL
     * Questo metodo controlla se l'email fornita esiste già nel database.
     */
    private void existingEmail( String email ) {
        boolean emailExists = false;
        if( email != null ) {
            emailExists = userRepository.existsByEmail( email );
        }
        if( emailExists ) throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGEMAIL );
    }


    /* EXISTING USER PROPERTIES
     * Questo metodo controlla se l'username e l'email forniti esistono già nel database.
     */
    private void existingUserProperties( String username, String email ) {
        existingUsername( username );
        existingEmail( email );
    }


    // DTO RETURNS

    /* GET USER DTO BY ID
     * Questo metodo restituisce un utente specifico in base al suo ID.
     */
    @Override
    public UserDTO getUserDTOById( Long id ) {
        User user = getUserById( id );
        return mapUserToDTO( user );
    }

    /* GET ALL USERSDTO PAGED
     * Questo metodo restituisce una lista di tutti gli utenti presenti nel database.
     */
    @Override
    public PagedResponseDTO<UserDTO> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir ) {

        // Ordinamento
        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();

        // Paginazione
        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );

        // Lista di tutti gli utenti
        Page<User> userPageList = userRepository.findAll( pageable );

        // Lista filtrata in base al potere dell'utente che effettua la richiesta
        // tutti quelli con poteri maggiori o uguali a quelli del richiedente
        List<User> userList = userPageList.getContent();

        // Lista di UserDTO
        List<UserDTO> userDTOList = userList.stream().map( this::mapUserToDTO ).toList();

        // PagedResponseDTO
        PagedResponseDTO<UserDTO> userResponseDTO = new PagedResponseDTO<>();

        userResponseDTO.setContent( userDTOList );

        userResponseDTO.setPageNo( userPageList.getNumber() );

        userResponseDTO.setPageSize( userPageList.getSize() );

        userResponseDTO.setTotalElements( userPageList.getTotalElements() );

        userResponseDTO.setTotalPages( userPageList.getTotalPages() );

        userResponseDTO.setLast( userPageList.isLast() );

        // Restituisce la lista di UserDTO
        return userResponseDTO;
    }

    @Override
    public PagedResponseDTO<UserDTO> getByEmailContains( String email, int pageNo, int pageSize, String sortBy, String sortDir ) {

        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();


        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );


        Page<User> userPageList = userRepository.findByEmailContainsIgnoreCase( pageable, email );


        List<User> userList = userPageList.getContent();


        List<UserDTO> userDTOList = userList.stream().map( this::mapUserToDTO ).toList();


        PagedResponseDTO<UserDTO> userResponseDTO = new PagedResponseDTO<>();

        userResponseDTO.setContent( userDTOList );

        userResponseDTO.setPageNo( userPageList.getNumber() );

        userResponseDTO.setPageSize( userPageList.getSize() );

        userResponseDTO.setTotalElements( userPageList.getTotalElements() );

        userResponseDTO.setTotalPages( userPageList.getTotalPages() );

        userResponseDTO.setLast( userPageList.isLast() );

        return userResponseDTO;

    }


    /* USER MAP TO DTO
     * Mapping manuale
     */
    public UserDTO mapUserToDTO( User user ) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId( user.getId() );
        userDTO.setUsername( user.getUsername() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setEnabled( user.isEnabled() );
        userDTO.setTemporaryPassword( user.isTemporaryPassword() );
        userDTO.setDateTokenCheck( user.getDateTokenCheck() );
        userDTO.setProfileName( String.valueOf( user.getProfile().getName() ) );

        Set<ProfilePermission> profilePermissions = profilePermissionRepository
                .findByProfileId( user.getProfile().getId() );

        Set<ProfilePermissionDTO> profilePermissionDTOS = profilePermissions
                .stream()
                .map( this::mapProfilePermissionToDTO ).collect( Collectors.toSet() );
        userDTO.setProfilePermissions( profilePermissionDTOS );

        return userDTO;

    }

    /* MAP USER PERMISSION TO DTO
     * Mappatura manuale
     */
    @Override
    public ProfilePermissionDTO mapProfilePermissionToDTO( ProfilePermission profilePermission ) {

        return ProfilePermissionDTO.builder()
                .id( profilePermission.getId() )
                .permissionName( profilePermission.getPermission().getName().name() )
                .valueRead( profilePermission.getValueRead() )
                .valueCreate( profilePermission.getValueCreate() )
                .valueUpdate( profilePermission.getValueUpdate() )
                .valueDelete( profilePermission.getValueDelete() )
                .build();
    }

    /* MODIFY USER
     * Modifica di un utente
     */
    @Override
    public UserDTO modifyUser( Long id, UserDTO userDTO ) {

        // Recupera l'utente dal database.
        User userToModify = getUserById( id );

        // Verifica se l'username e l'email forniti esistono già nel database.
        existingUserProperties( userDTO.getUsername(), userDTO.getEmail() );

        // Imposta l'username e l'email dell'utente in base ai valori forniti.
        setUsernameAndEmail( userDTO, userToModify );

        // Salva l'utente nel database.
        User userSaved = userRepository.save( userToModify );

        // Restituisce l'utente.
        return mapUserToDTO( userSaved );
    }

    /* GET PROFILE PERMISSIONS BY USER ID
     * Questo metodo permette di ricevere una lista di ProfilePermissions in base all'id dell'utente
     */
    @Override
    public Set<ProfilePermission> getProfilePermissionsByUserId( Long userId ) {

        return profilePermissionRepository.findByProfileId( userId );
    }

    @Override
    public Set<ProfilePermissionDTO> findByProfileIdDTO( Long profileId ) {
        Set<ProfilePermission> profilePermissions = profilePermissionRepository.findByProfileId( profileId );

        return profilePermissions.stream()
                .map( this::mapProfilePermissionToDTO )
                .collect( Collectors.toSet() );
    }

    public ProfileDTO getProfileByUserId( Long userId ) {

        Profile profile = profileRepository.findByUserId( userId );
        Set<PermissionDTO> permissionDTOS = profile.getProfilePermissions().stream().map( permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setId( permission.getId() );
            permissionDTO.setName( permission.getPermission().getName() );
            return permissionDTO;
        } ).collect( Collectors.toSet() );

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId( profile.getUser().getId() );
        profileDTO.setName( String.valueOf( profile.getName() ) );
        profileDTO.setPower( profile.getPower() );
        profileDTO.setPermissions( permissionDTOS );

        return profileDTO;
    }


    // METODI INTERNI

    /* MAP PROFILE PERMISSION DTO TO ENTITY
     * Mappatura manuale
     */
    private ProfilePermission mapProfilePermissionDTOToEntity( ProfilePermissionDTO profilePermissionDTO ) {
        User user = userRepository.findById( profilePermissionDTO.getId() )
                .orElseThrow( () -> new ResourceNotFoundException( ErrorCodeList.NF404 ) );

        Profile profile = profileRepository.findById( user )
                .orElseThrow( () -> new ResourceNotFoundException( ErrorCodeList.NF404 ) );

        Permission permission = permissionRepository.findByName( PermissionList.valueOf( profilePermissionDTO.getPermissionName() ) )
                .orElseThrow( () -> new ResourceNotFoundException( ErrorCodeList.NF404 ) );


        return ProfilePermission.builder()
                .id( profilePermissionDTO.getId() )
                .profile( profile )
                .permission( permission )
                .valueRead( profilePermissionDTO.getValueRead() )
                .valueCreate( profilePermissionDTO.getValueCreate() )
                .valueUpdate( profilePermissionDTO.getValueUpdate() )
                .valueDelete( profilePermissionDTO.getValueDelete() )
                .build();
    }

    /* GET USER BY ID
     * Questo metodo recupera un utente dal database in base al suo ID.
     */
    private User getUserById( Long id ) {
        return userRepository.findById( id ).orElseThrow( () -> new ResourceNotFoundException(
                ErrorCodeList.NF404 ) );
    }


    /* SET USERNAME AND EMAIL
     *Questo metodo accetta uno UserDTO e uno User e setta le proprietà di User in base alle informazioni
     * ricavate dall'oggetto userDTO
     */
    private void setUsernameAndEmail( UserDTO userDTO, User user ) {
        user.setUsername( userDTO.getUsername() == null ? user.getUsername() : userDTO.getUsername() );
        user.setEmail( userDTO.getEmail() == null ? user.getEmail() : userDTO.getEmail() );
    }
}
