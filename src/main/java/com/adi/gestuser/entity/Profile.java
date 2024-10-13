package com.adi.gestuser.entity;

import com.adi.gestuser.enums.ProfileList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PROFILES")
public class Profile {
    @Id
    private Long id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    // QUESTO E' UN ENUM ProfileList
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private ProfileList name;

    private int power;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<ProfilePermission> profilePermissions;


    public Profile( ProfileList profileName ) {

        this.name = profileName;

        switch (this.name) {

            case ADMIN -> this.power = 0;

            case USER -> this.power = 10;

            default -> this.power = 100;
        }

    }

}
