package com.adi.gestuser.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(SecurityProperties securityProperties, PasswordEncoder passwordEncoder) {
        this.securityProperties = securityProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String REALM = "REAME";

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName(REALM)
                        .authenticationEntryPoint(getBasicAuthEntryPoint())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public BasicAuthenticationEntryPoint getBasicAuthEntryPoint() {
        AuthEntryPoint entryPoint = new AuthEntryPoint();
        entryPoint.setRealmName("REAME");
        return entryPoint;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        UserDetails readUser = User.builder()
                .username(securityProperties.getRead().getUsername())
                .password(passwordEncoder.encode(securityProperties.getRead().getPassword()))
                .roles("USER")
                .build();

        UserDetails writeUser = User.builder()
                .username(securityProperties.getWrite().getUsername())
                .password(passwordEncoder.encode(securityProperties.getWrite().getPassword()))
                .roles("USER", "ADMIN")
                .build();

        manager.createUser(readUser);
        manager.createUser(writeUser);

        return manager;
    }
}

