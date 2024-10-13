package com.adi.gestuser.utils;

import com.adi.gestuser.entity.Permission;
import com.adi.gestuser.enums.PermissionList;
import com.adi.gestuser.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionsDataInizializer {

    private static final Logger logger = LoggerFactory.getLogger( PermissionsDataInizializer.class );

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionsDataInizializer( PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @PostConstruct
    public void initPermissions() {
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
}
