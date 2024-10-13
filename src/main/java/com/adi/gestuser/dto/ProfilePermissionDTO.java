package com.adi.gestuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePermissionDTO {

    private Long id;

    private String permissionName;

    private int valueRead;

    private int valueCreate;

    private int valueUpdate;


    private int valueDelete;


}
