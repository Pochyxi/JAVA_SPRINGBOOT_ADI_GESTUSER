package com.adi.gestuser.entity;

import com.adi.gestuser.enums.ApikeyRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "APIKEYS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey {

    @Id
    private String apikey;
    private LocalDateTime expireDate;

    @Enumerated(EnumType.STRING)
    private ApikeyRole apikeyRole;
}
