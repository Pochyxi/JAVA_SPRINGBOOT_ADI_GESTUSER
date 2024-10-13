package com.adi.gestuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

 private Long userId;

 private String name;

 private int power;

 private Set<PermissionDTO> permissions;

}
