package com.adi.gestuser.security;

import com.adi.gestuser.entity.User;
import com.adi.gestuser.exception.ErrorCodeList;
import com.adi.gestuser.exception.ResourceNotFoundException;
import com.adi.gestuser.exception.appException;
import com.adi.gestuser.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {

    UserService userService;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app-jwt-expiration-milliseconds}")
    private long jwtExpirationInMs;

    @Autowired
    public JwtTokenProvider( UserService userService) {
        this.userService = userService;
    }

    /*** GENERATE TOKEN
     * Questo metodo genera il token.
     * Il token contiene l'username, la data di creazione, la data di scadenza e i permessi dell'utente.
     ***/
    public String generateToken( Authentication authentication ) {

        String username = authentication.getName();

        Date currentDate = new Date();
        Date expirationDate = new Date( currentDate.getTime() + jwtExpirationInMs );



        return Jwts.builder()
                .setSubject( username )
                .setIssuedAt( currentDate )
                .setExpiration( expirationDate )
                .signWith( key() )
                .compact();
    }


    /*** KEY
     * Questo metodo recupera la chiave segreta dal file properties.
     * La chiave viene convertita in un oggetto Key.
     ***/
    private Key key() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode( jwtSecret )
        );
    }


    /*** GET USERNAME FROM JWT
     Questo metodo recupera l'username dell'utente dal token.
     ***/
    public String getUsernameFromJWT( String token ) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey( key() )
                .build()
                .parseClaimsJws( token )
                .getBody();

        return claims.getSubject();
    }

    /*** VALIDATE TOKEN
     Questo metodo controlla se il token è valido.
     ***/
    public boolean validateToken( String authToken ) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey( key() )
                    .build()
                    .parseClaimsJws( authToken )
                    .getBody();


            Date issuedAt = claims.getIssuedAt();
            String username = claims.getSubject();

            LocalDateTime issuedAtLocalDateTime = issuedAt.toInstant()
                    .atZone( ZoneId.systemDefault())
                    .toLocalDateTime();

            User user = userService.findByUsernameOrEmail(username, username).orElseThrow(
                    () -> new ResourceNotFoundException( ErrorCodeList.NF404)
            );

            if ( issuedAtLocalDateTime.isBefore(user.getDateTokenCheck())) throw new appException(
                    HttpStatus.BAD_REQUEST, ErrorCodeList.TOKENOBSOLETE
            );



            return true;

        } catch( MalformedJwtException ex ) {
            System.out.println( "Token non valido" );
        } catch( ExpiredJwtException ex ) {
            System.out.println( "Token JWT scaduto" );
        } catch( UnsupportedJwtException ex ) {
            System.out.println( "Token JWT non supportato" );
        } catch( IllegalArgumentException ex ) {
            System.out.println( "Token JWT vuoto" );
        }
        return false;
    }

}
