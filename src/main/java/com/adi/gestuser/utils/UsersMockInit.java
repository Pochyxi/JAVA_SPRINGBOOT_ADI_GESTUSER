package com.adi.gestuser.utils;
import com.adi.gestuser.dto.SignupDTO;
import com.adi.gestuser.entity.Permission;
import com.adi.gestuser.entity.Profile;
import com.adi.gestuser.entity.ProfilePermission;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.enums.PermissionList;
import com.adi.gestuser.enums.ProfileList;
import com.adi.gestuser.repository.PermissionRepository;
import com.adi.gestuser.repository.ProfilePermissionRepository;
import com.adi.gestuser.repository.ProfileRepository;
import com.adi.gestuser.repository.UserRepository;
import com.adi.gestuser.service.AuthenticationService;
import com.adi.gestuser.service.UserService;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class UsersMockInit {

    private static final Logger logger = LoggerFactory.getLogger(UsersMockInit.class);

    AuthenticationService authenticationService;

    UserService userService;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    private final PermissionRepository permissionRepository;
    private final ProfilePermissionRepository profilePermissionRepository;


    @Autowired
    public UsersMockInit( UserService userService,
                          ProfileRepository profileRepository,
                          AuthenticationService authenticationService,
                          UserRepository userRepository,
                          PermissionRepository permissionRepository,
                          ProfilePermissionRepository profilePermissionRepository ) {
        this.userService = userService;
        this.profileRepository = profileRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.profilePermissionRepository = profilePermissionRepository;
    }

    @PostConstruct
    public void initUsers() {
        initPermissions();

        String darioMail = "dario@dad.it";
        String darioUsername = "Dario";

        String adminMail = "Admin@gmail.com";
        String adminUsername = "Admin";

        String adiMail = "Adiener@gmail.com";
        String adiUsername = "Adiener";

        SignupDTO admin = new SignupDTO();
        admin.setEmail( adminMail );
        admin.setUsername( adminUsername );

        SignupDTO adi = new SignupDTO();
        adi.setEmail( adiMail );
        adi.setUsername( adiUsername );

        SignupDTO dario = new SignupDTO();
        dario.setEmail( darioMail );
        dario.setUsername( darioUsername );

        List<SignupDTO> listofAdminUsers = List.of( admin, adi );
        List<SignupDTO> listofUsers = List.of( dario );

        createUsers( listofAdminUsers, listofUsers );
    }

    private void initPermissions() {
        for ( PermissionList perm : PermissionList.values()) {
            permissionRepository.findByName(perm)
                    .orElseGet(() -> {
                        Permission newPerm = new Permission();
                        newPerm.setName(perm);
                        System.out.println("Permesso " + perm + " Creato");
                        return permissionRepository.save(newPerm);
                    });
        }

        logger.info("PERMESSI INIZIALIZZATI CORRETTAMENTE");
    }

    private void createUsers( List<SignupDTO> listofAdminUsers, List<SignupDTO> listofUsers ) {

        Faker faker = new Faker();


        for( int i = 0 ; userRepository.count() < 101 ; i++ ) {
            logger.info( "Utenti inizializzati: {}", i );

            if( i == 0 ) {
                for( SignupDTO signupDTO : listofAdminUsers ) {
                    User userCreated = checkAndCreateUser( signupDTO.getEmail(), signupDTO.getUsername(), i );

                    if( userCreated != null ) {
                        Profile profile = unlockAdmin( userCreated.getEmail() );
                        setAllPermissions( profile );
                    }

                }

                for( SignupDTO signupDTO : listofUsers ) {
                    User userCreated = checkAndCreateUser( signupDTO.getEmail(), signupDTO.getUsername(), i );

                    if( userCreated != null ) {
                        Profile profile = unlockUser( userCreated.getEmail() );
                        setUserAllPermissions( profile );
                    }
                }

                continue;
            }


            String email = faker.internet().emailAddress();
            String username = faker.name().username();

            checkAndCreateUser( email, username, i );
        }
    }

    private Profile unlockAdmin( String email) {
        // Sblocco utente Admin
        User userAdmin = userRepository.findByEmail( email ).orElseThrow();
        userAdmin.setEnabled(true);
        userAdmin.setTemporaryPassword( false );

        // Creazione profilo Admin
        Profile profileAdmin = userAdmin.getProfile();
        profileAdmin.setName( ProfileList.ADMIN );
        profileAdmin.setPower( 0 );
        userAdmin.setProfile( profileAdmin );
        User userSaved = userRepository.save( userAdmin );
        return userSaved.getProfile();
    }

    private Profile unlockUser( String email) {
        // Sblocco utente User
        User userAdmin = userRepository.findByEmail( email ).orElseThrow();
        userAdmin.setEnabled(true);
        userAdmin.setTemporaryPassword( false );

        // Creazione profilo User
        Profile profileAdmin = userAdmin.getProfile();
        profileAdmin.setName( ProfileList.USER );
        profileAdmin.setPower( 1 );
        userAdmin.setProfile( profileAdmin );
        User userSaved = userRepository.save( userAdmin );
        return userSaved.getProfile();
    }

    private void setAllPermissions( Profile profileAdmin ) {
        for( PermissionList permissionName : PermissionList.values() ) {
            giveAllPermissions( profileAdmin.getId(), permissionName );
        }
    }

    private void setUserAllPermissions( Profile profileUser ) {

        giveAllPermissions( profileUser.getId(), PermissionList.USER );
    }

    private User checkAndCreateUser( String email, String username, int i ) {

        boolean userCheck = userService.existsByUsernameOrEmail( username, email );

        if( !userCheck ) {
            logger.info( "[" + i + "] Utente inizializzato: " + email );
            return createUser( email, username );
        } else {
            logger.warn( "Utente ESISTENTE: " + email );
        }

        return null;
    }

    private User createUser( String email, String username ) {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail( email );
        signupDTO.setUsername( username );


        return authenticationService.createUser( signupDTO, false );
    }


    private void giveAllPermissions(
            Long profileId,
            PermissionList permissionName
    ) {
        Set<ProfilePermission> profilePermissions = profilePermissionRepository.findByProfileId( profileId );

        for( ProfilePermission profilePermission : profilePermissions ) {
            if( profilePermission.getPermission().getName().equals( permissionName ) ) {
                return;
            }
        }

        Profile profile = profileRepository.findByUserId( profileId );
        Optional<Permission> permission = permissionRepository.findByName( permissionName );

        if( permission.isEmpty() ) {
            logger.warn( "Permesso non trovato: {}", permissionName );
            return;
        }

        Permission permissionEntity = permission.get();

        ProfilePermission profilePermission = ProfilePermission
                .builder()
                .profile( profile )
                .permission( permissionEntity )
                .valueCreate( 1 )
                .valueRead( 1 )
                .valueUpdate( 1 )
                .valueDelete( 1 )
                .build();

        profilePermissionRepository.save( profilePermission );
    }

}
