package com.adi.gestuser.repository;

import com.adi.gestuser.entity.Permission;
import com.adi.gestuser.enums.PermissionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {


    Optional<Permission> findByName( PermissionList name);
}

