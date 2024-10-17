package com.adi.gestuser.security;

import com.adi.gestuser.entity.ApiKey;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class ApiKeyAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final ApiKey apiKey;

    public ApiKeyAuthenticationToken( Object principal, Object credentials,
                                      Collection<? extends GrantedAuthority> authorities, ApiKey apiKey) {
        super(principal, credentials, authorities);
        this.apiKey = apiKey;
    }
}
