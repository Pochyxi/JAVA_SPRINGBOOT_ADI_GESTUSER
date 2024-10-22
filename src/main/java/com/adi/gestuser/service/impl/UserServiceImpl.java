package com.adi.gestuser.service.impl;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.*;
import com.adi.gestuser.exception.ErrorCodeList;
import com.adi.gestuser.exception.ResourceNotFoundException;
import com.adi.gestuser.exception.appException;
import com.adi.gestuser.repository.*;
import com.adi.gestuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final ProfilePermissionRepository profilePermissionRepository;


    //**** VOID RETURNS ****//

    /**
     * CREATE USER
     * @param user utente da creare
     */
    @Override
    public void createUser( User user ) {
        userRepository.save( user );
    }

    /**
     * DELETE USER
     * @param id id dell'utente da eliminare
     */
    @Override
    public void deleteUser( Long id ) {

        User user = getUserById( id );

        userRepository.delete( user );
    }


    //**** USER RETURNS ****//

    /**
     * SAVE
     * @param user utente da salvare
     */
    @Override
    public User save( User user ) {
        return userRepository.save( user );
    }

    /**
     * FIND BY ID
     * @param id id dell'utente
     */
    @Override
    public User findById( Long id ) {

        return userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( ErrorCodeList.NF404 ) );
    }

    /**
     * FIND BY EMAIL
     * @param email email dell'utente
     */
    @Override
    public Optional<User> findByEmail( String email ) {

        return userRepository.findByEmail( email );
    }

    /**
     * FIND BY USERNAME OR EMAIL
     * @param username username dell'utente
     * @param email email dell'utente
     */
    @Override
    public Optional<User> findByUsernameOrEmail( String username, String email ) {
        return userRepository.findByUsernameOrEmail( username, email );
    }

    /**
     * FIND DTO BY USERNAME OR EMAIL
     * @param username username dell'utente
     * @param email email dell'utente
     */
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


    //**** BOOLEAN RETURNS ****//

    /**
     * EXISTS BY USERNAME
     * @param username username dell'utente
     */
    @Override
    public Boolean existsByUsername( String username ) {

        return userRepository.existsByUsername( username );
    }


    /**
     * EXISTS BY EMAIL
     * @param email email dell'utente
     */
    @Override
    public Boolean existsByEmail( String email ) {

        return userRepository.existsByEmail( email );
    }


    /**
     * EXISTS BY USERNAME OR EMAIL
     * @param username username dell'utente
     * @param email email dell'utente
     */
    @Override
    public Boolean existsByUsernameOrEmail( String username, String email ) {
        return userRepository.existsByUsernameOrEmail( username, email );
    }

    /**
     * EXISTING USERNAME
     * @param username username dell'utente
     */
    private void existingUsername( String username ) {
        boolean usernameExists = false;
        if( username != null ) {
            usernameExists = userRepository.existsByUsername( username );
        }
        if( usernameExists ) throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGUSERNAME );
    }


    /**
     * EXISTING EMAIL
     * @param email email dell'utente
     */
    private void existingEmail( String email ) {
        boolean emailExists = false;
        if( email != null ) {
            emailExists = userRepository.existsByEmail( email );
        }
        if( emailExists ) throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTINGEMAIL );
    }


    /**
     * EXISTING USER PROPERTIES
     * @param username username dell'utente
     * @param email email dell'utente
     */
    private void existingUserProperties( String username, String email ) {
        existingUsername( username );
        existingEmail( email );
    }


    //**** DTO RETURNS ****//

    /**
     * GET USER DTO BY ID
     * @param id id dell'utente
     */
    @Override
    public UserDTO getUserDTOById( Long id ) {
        User user = getUserById( id );
        return mapUserToDTO( user );
    }

    /**
     * GET ALL USERS
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy ordinamento
     * @param sortDir direzione dell'ordinamento
     * @param powerOfUser potere dell'utente
     */
    @Override
    public PagedResponseDTO<UserDTO> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir, int powerOfUser ) {

        // Ordinamento
        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();

        // Paginazione
        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );

        // Lista di tutti gli utenti
        Page<User> userPageList =
                userRepository.findAllByProfilePowerGreaterThanEqual( powerOfUser, pageable );

        return makePagedResponse( userPageList );
    }


    /**
     * GET BY EMAIL CONTAINS
     * @param email email dell'utente
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy ordinamento
     * @param sortDir direzione dell'ordinamento
     */
    @Override
    public PagedResponseDTO<UserDTO> getByEmailContains( String email, int pageNo, int pageSize, String sortBy, String sortDir ) {

        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();


        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );


        Page<User> userPageList = userRepository.findByEmailContainsIgnoreCase( pageable, email );


        return makePagedResponse( userPageList );
    }


    /**
     * MAP USER TO DTO
     * @param user utente
     * @return oggetto UserDTO
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

    /**
     * MAP PROFILE PERMISSION TO DTO
     * @param profilePermission permessi del profilo
     * @return oggetto ProfilePermissionDTO
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


    /**
     * MODIFY USER
     * @param id id dell'utente
     * @param userDTO oggetto UserDTO
     * @return oggetto UserDTO
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


    /**
     * FIND BY PROFILE ID DTO
     * @param profileId id del profilo
     * @return insieme di ProfilePermissionDTO
     */
    @Override
    public Set<ProfilePermissionDTO> findByProfileIdDTO( Long profileId ) {
        Set<ProfilePermission> profilePermissions = profilePermissionRepository.findByProfileId( profileId );

        return profilePermissions.stream()
                .map( this::mapProfilePermissionToDTO )
                .collect( Collectors.toSet() );
    }


    /**
     * GET PROFILE BY USER ID
     * @param userId id dell'utente
     * @return oggetto ProfileDTO
     */
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


    //**** METODI INTERNI ****//

    /**
     * GET USER BY ID
     * @param id id dell'utente
     * @return oggetto User
     */
    private User getUserById( Long id ) {
        return userRepository.findById( id ).orElseThrow( () -> new ResourceNotFoundException(
                ErrorCodeList.NF404 ) );
    }


    /**
     * SET USERNAME AND EMAIL
     * @param userDTO oggetto UserDTO
     * @param user utente
     */
    private void setUsernameAndEmail( UserDTO userDTO, User user ) {
        user.setUsername( userDTO.getUsername() == null ? user.getUsername() : userDTO.getUsername() );
        user.setEmail( userDTO.getEmail() == null ? user.getEmail() : userDTO.getEmail() );
    }


    /**
     * MAKE PAGED RESPONSE, in base alla lista di utenti restituisce un oggetto PagedResponseDTO
     * @param userPageList lista di utenti
     * @return oggetto PagedResponseDTO
     */
    private PagedResponseDTO<UserDTO> makePagedResponse( Page<User> userPageList ) {
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
}
