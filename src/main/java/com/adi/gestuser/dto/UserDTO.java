package com.adi.gestuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private boolean isEnabled;

    private boolean isTemporaryPassword;

    private LocalDateTime dateTokenCheck;

    private String profileName;

    private Set<ProfilePermissionDTO> profilePermissions;

}
