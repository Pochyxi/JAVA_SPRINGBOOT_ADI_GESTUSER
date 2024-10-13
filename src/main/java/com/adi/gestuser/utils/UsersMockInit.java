package com.adi.gestuser.utils;
import com.adi.gestuser.dto.SignupDTO;
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
        createUsers();

        User admin = unlockUser();


        setAllPermissions( admin.getProfile() );
    }

    private void createUsers() {

        Faker faker = new Faker();

        String adminMail = "Admin@gmail.com";
        String adminUsername = "Admin";

        String adiMail = "Adiener@gmail.com";
        String adiUsername = "Adiener";


        for( int i = 0 ; userRepository.count() < 101 ; i++ ) {
            logger.info( "Utenti inizializzati: " + i );

            if( i == 0 ) {
                checkAndCreateUser( adminMail, adminUsername, i );
                continue;
            }
            if( i == 1 ) {
                checkAndCreateUser( adiMail, adiUsername, i );
                continue;
            }


            String email = faker.internet().emailAddress();
            String username = faker.name().username();

            checkAndCreateUser( email, username, i );

        }
    }

    private User unlockUser() {
        // Sblocco utente Admin
        User userAdmin = userRepository.findByEmail( "Adiener@gmail.com" ).orElseThrow();
        userAdmin.setEnabled(true);
        userAdmin.setTemporaryPassword( false );

        // Creazione profilo Admin
        Profile profileAdmin = userAdmin.getProfile();
        profileAdmin.setName( ProfileList.ADMIN );
        profileAdmin.setPower( 0 );
        profileRepository.save( profileAdmin );

        userAdmin.setProfile( profileAdmin );
        return userRepository.save( userAdmin );
    }

    private void setAllPermissions( Profile profileAdmin ) {
        for( PermissionList permissionName : PermissionList.values() ) {
            giveAllPermissions( profileAdmin.getId(), permissionName );
        }
    }

    private void checkAndCreateUser( String email, String username, int i ) {

        boolean userCheck = userService.existsByUsernameOrEmail( username, email );

        if( !userCheck ) {
            createUser( email, username );
            logger.info( "[" + i + "] Utente inizializzato: " + email );
        } else {
            logger.warn( "Utente ESISTENTE: " + email );
        }
    }

    private void createUser( String email, String username ) {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail( email );
        signupDTO.setUsername( username );


        authenticationService.createUser( signupDTO, false );
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

        ProfilePermission profilePermission = ProfilePermission
                .builder()
                .profile( profile )
                .permission( permissionRepository.findByName( permissionName ).orElseThrow() )
                .valueCreate( 1 )
                .valueRead( 1 )
                .valueUpdate( 1 )
                .valueDelete( 1 )
                .build();

        profilePermissionRepository.save( profilePermission );
    }

}
