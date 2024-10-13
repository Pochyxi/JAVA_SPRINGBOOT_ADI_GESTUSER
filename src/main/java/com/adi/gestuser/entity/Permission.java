package com.adi.gestuser.entity;

import com.adi.gestuser.enums.PermissionList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "PERMISSIONS")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PermissionList name;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    private Set<ProfilePermission> profilePermissions;

}
