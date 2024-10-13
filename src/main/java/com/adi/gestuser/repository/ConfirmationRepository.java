package com.adi.gestuser.repository;

import com.adi.gestuser.entity.Confirmation;
import com.adi.gestuser.entity.User;
import com.adi.gestuser.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {

    Confirmation findByToken( String token);

    Set<Confirmation> findByTokenTypeAndUserId( TokenType tokenType, Long userId);

    Set<Confirmation> findByUserId( Long userId);

    boolean existsConfirmationByUser( User user);
}
