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

import java.util.ArrayList;
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

    List<SignupDTO> listofAdminUsers = new ArrayList<>();
    List<SignupDTO> listofUsers = new ArrayList<>();

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

        populateListOfUsersOrAdmin( listofAdminUsers, adminMail, adminUsername );
        populateListOfUsersOrAdmin( listofAdminUsers, adiMail, adiUsername );
        populateListOfUsersOrAdmin( listofUsers, darioMail, darioUsername );

        createUsers( listofAdminUsers, listofUsers );
    }

    // Metodo per popolare la lista degli utenti, sia admin che users
    private void populateListOfUsersOrAdmin( List<SignupDTO> list, String email, String username ) {
        SignupDTO dto = SignupDTO.builder()
                .email( email )
                .username( username )
                .build();

        list.add( dto );
    }

    // Inizializzazione di tutti i permessi
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


    /**
     * Metodo per creare gli utenti
     * @param listofAdminUsers la lista degli utenti admin
     * @param listofUsers la lista degli utenti
     */
    private void createUsers( List<SignupDTO> listofAdminUsers, List<SignupDTO> listofUsers ) {

        // Creo un faker per generare email e username
        Faker faker = new Faker();


        // Creo 100 utenti di cui all'indice 0 verranno creati gi admin
        // e gli utenti di esempio abilitati
        for( int i = 0 ; userRepository.count() < 101 ; i++ ) {
            logger.info( "Utenti inizializzati: {}", i );

            if( i == 0 ) {
                for( SignupDTO signupDTO : listofAdminUsers ) {
                    User userCreated = checkAndCreateUser( signupDTO.getEmail(), signupDTO.getUsername(), i );

                    if( userCreated != null ) {
                        Profile profile = unlockUserSetProfile( userCreated.getEmail(), ProfileList.ADMIN );
                        setAllPermissions( profile );
                    }

                }

                for( SignupDTO signupDTO : listofUsers ) {
                    User userCreated = checkAndCreateUser( signupDTO.getEmail(), signupDTO.getUsername(), i );

                    if( userCreated != null ) {
                        Profile profile = unlockUserSetProfile( userCreated.getEmail(), ProfileList.USER );

                        if( profile != null ) {
                            setUserAllPermissions( profile );

                        }
                    }
                }

                continue;
            }


            String email = faker.internet().emailAddress();
            String username = faker.name().username();

            checkAndCreateUser( email, username, i );
        }
    }

    /**
     * Metodo per sbloccare un utente e settare il profilo
     * Dato che esistono solo 2 profili, il potere sarà o 0 o 10
     * @param email email dell'utente
     * @param profileList il profilo da settare
     * @return il profilo settato
     */
    private Profile unlockUserSetProfile(String email, ProfileList profileList) {
        Optional<User> userFound = userRepository.findByEmail(email);

        if( userFound.isEmpty() ) {
            logger.warn("Utente non trovato: {}", email);
            return null;
        }

        User userToUnlock = userFound.get();
        userToUnlock.setEnabled(true);
        userToUnlock.setTemporaryPassword(false);

        Profile profile = userToUnlock.getProfile();
        profile.setName(profileList);
        int profilePower = profileList.equals( ProfileList.ADMIN ) ? 0 : 10;
        profile.setPower(profilePower);

        userToUnlock.setProfile(profile);

        User userSaved = userRepository.save(userToUnlock);

        return userSaved.getProfile();
    }


    /**
     * Metodo per fornire tutti i permessi ad un profilo ADMIN
     * @param profileAdmin il profilo admin
     */
    private void setAllPermissions( Profile profileAdmin ) {
        for( PermissionList permissionName : PermissionList.values() ) {
            giveAllPermissions( profileAdmin.getId(), permissionName );
        }
    }

    /**
     * Metodo per fornire tutti i permessi USER ad un profilo
     * @param profileUser il profilo user
     */
    private void setUserAllPermissions( Profile profileUser ) {

        giveAllPermissions( profileUser.getId(), PermissionList.USER );
    }

    /**
     * Metodo per controllare se un utente esiste e crearlo
     * @param email email dell'utente
     * @param username username dell'utente
     * @param i indice nel ciclo for(solo per logging)
     * @return User appena creato
     */
    private User checkAndCreateUser( String email, String username, int i ) {

        boolean userCheck = userService.existsByUsernameOrEmail( username, email );

        if( !userCheck ) {
            logger.info( "[{}] Utente inizializzato: {}", i, email );
            return createUser( email, username );
        } else {
            logger.warn( "Utente ESISTENTE: {}", email );
        }

        return null;
    }

    /**
     * Metodo per creare un utente
     * @param email email dell'utente
     * @param username username dell'utente
     * @return User appena creato
     */
    private User createUser( String email, String username ) {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setEmail( email );
        signupDTO.setUsername( username );


        return authenticationService.createUser( signupDTO, false );
    }


    /**
     * Metodo per dare tutti i permessi ad un profilo
     * @param profileId id del profilo
     * @param permissionName nome del permesso
     */
    private void giveAllPermissions(
            Long profileId,
            PermissionList permissionName
    ) {
        Set<ProfilePermission> profilePermissions = profilePermissionRepository.findByProfileId( profileId );

        // Ciclo su tutti i permessi disponibili
        // Se il permesso è già presente, non fare nulla
        for( ProfilePermission profilePermission : profilePermissions ) {
            if( profilePermission.getPermission().getName().equals( permissionName ) ) {
                return;
            }
        }


        // Cerco il profilo dell'utente per il collegamento
        Profile profile = profileRepository.findByUserId( profileId );

        // Cerco il permesso per il collegamento
        Optional<Permission> permission = permissionRepository.findByName( permissionName );

        if( permission.isEmpty() ) {
            logger.warn( "Permesso non trovato: {}", permissionName );
            return;
        }

        Permission permissionEntity = permission.get();

        // Creo il collegamento settando tutte le flag a TRUE(1)
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
