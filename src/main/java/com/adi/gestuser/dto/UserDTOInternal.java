package com.adi.gestuser.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDTOInternal {
    private Long id;

    private String username;

    private String email;

    private boolean isEnabled;

    private boolean isTemporaryPassword;

    private LocalDateTime dateTokenCheck;

    private String profileName;

    private Set<ProfilePermissionDTO> profilePermissions;

    private String password;
}
