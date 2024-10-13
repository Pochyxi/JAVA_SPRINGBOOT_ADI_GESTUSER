package com.adi.gestuser.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PROFILE_PERMISSIONS")
public class ProfilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Column(name = "read_flag")
    private int valueRead = 0;

    @Column(name = "create_flag")
    private int valueCreate = 0;

    @Column(name = "update_flag")
    private int valueUpdate = 0;

    @Column(name = "delete_flag")
    private int valueDelete = 0;

}
