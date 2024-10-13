package com.adi.gestuser.dto;

import com.adi.gestuser.enums.PermissionList;
import lombok.Data;

@Data
public class PermissionDTO {

  private Long id;

  private PermissionList name;

}
