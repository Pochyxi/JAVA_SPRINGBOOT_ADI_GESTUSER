package com.adi.gestuser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor

@Entity
@Table(name = "USERS")
public class User {
    // todo: possibilità di aggiungere @Version

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    private boolean isTemporaryPassword;

    private boolean isEnabled;

    @Temporal( TemporalType.TIMESTAMP )
    @CreatedDate
    private LocalDateTime dateTokenCheck;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Confirmation> confirmation;

    public User(){
        this.dateTokenCheck= LocalDateTime.now();
    }

}
