package com.adi.gestuser.service;

import com.adi.gestuser.dto.ChangePasswordDTO;
import com.adi.gestuser.dto.JwtAuthResponseDTO;
import com.adi.gestuser.dto.LoginDTO;
import com.adi.gestuser.dto.SignupDTO;
import com.adi.gestuser.entity.Confirmation;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.enums.TokenType;

public interface AuthenticationService {

    JwtAuthResponseDTO login( LoginDTO loginDTO);

    User createUser( SignupDTO signupDTO, boolean confEmail);

    void changeEmail( Long userId, String email);

    void changePassword( ChangePasswordDTO changePasswordDTO, String token);

    Confirmation verifyToken( String token, TokenType tokenType );

    void resetPasswordRequest(String email);

    void resendVerificationRequest(Long userId);
}
