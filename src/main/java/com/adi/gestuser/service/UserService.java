package com.adi.gestuser.service;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.ProfilePermission;
import com.adi.gestuser.entity.User;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    // VOID RETURNS
    void createUser(User user);
    void deleteUser(Long id );

    // MAPPING
    UserDTO mapUserToDTO( User user);


    // ENTITY RETURNS
    User save( User user );

    User findById( Long id );

    Optional<User> findByEmail( String email );

    Optional<User> findByUsernameOrEmail( String username, String email );

    // Questo metodo permette di ottenere i dati di un utente per username o email, restituisce anche la password crittografata
    UserDTOInternal findDTOByUsernameOrEmail( String username, String email );

    // BOOLEAN RETURNS
    Boolean existsByUsername( String username );

    Boolean existsByEmail( String email );

    Boolean existsByUsernameOrEmail( String username, String email );


    // DTO RETURNS
    UserDTO getUserDTOById( Long id );

    PagedResponseDTO<UserDTO> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir, int powerOfUser);

    PagedResponseDTO<UserDTO> getByEmailContains( String email, int pageNo, int pageSize, String sortBy, String sortDir);

    ProfilePermissionDTO mapProfilePermissionToDTO( ProfilePermission profilePermission );

    UserDTO modifyUser( Long id, UserDTO userDTO );

    Set<ProfilePermissionDTO> findByProfileIdDTO( Long profileId );

    ProfileDTO getProfileByUserId( Long userId );
}
