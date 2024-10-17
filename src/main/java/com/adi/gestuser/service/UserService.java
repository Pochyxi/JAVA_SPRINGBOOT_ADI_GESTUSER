package com.adi.gestuser.service;

import com.adi.gestuser.dto.*;
import com.adi.gestuser.entity.ProfilePermission;
import com.adi.gestuser.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    // VOID RETURNS
    void createUser(User user);
    void deleteUser(Long id );

    // MAPPING
    User mapUserDTOToEntity( UserDTO userDTO);

    UserDTO mapUserToDTO( User user);


    // ENTITY RETURNS
    User save( User user );

    User findById( Long id );

    Optional<User> findByEmail( String email );

    Optional<User> findByUsernameOrEmail( String username, String email );

    UserDTOInternal findDTOByUsernameOrEmail( String username, String email );

    User getUserByAuthentication();

    Page<User> findByProfilePowerGreaterThanEqual( int power, Pageable page);

    Set<ProfilePermission> getProfilePermissionsByUserId( Long userId );


    // BOOLEAN RETURNS
    Boolean existsByUsername( String username );

    Boolean existsByEmail( String email );

    Boolean existsByUsernameOrEmail( String username, String email );


    // DTO RETURNS
    UserDTO getUserDTOById( Long id );

    PagedResponseDTO<UserDTO> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir );

    PagedResponseDTO<UserDTO> getByEmailContains( String email, int pageNo, int pageSize, String sortBy, String sortDir);

    ProfilePermissionDTO mapProfilePermissionToDTO( ProfilePermission profilePermission );

    UserDTO modifyUser( Long id, UserDTO userDTO );

    Set<ProfilePermissionDTO> findByProfileIdDTO( Long profileId );

    ProfileDTO getProfileByUserId( Long userId );
}
