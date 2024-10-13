package com.adi.gestuser.service;

public interface PreAuthService {

    boolean userHasPowerOnSubject(Long subjectId, String permissionName );
}
