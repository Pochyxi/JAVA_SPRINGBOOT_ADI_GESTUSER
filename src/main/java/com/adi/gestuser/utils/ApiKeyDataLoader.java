package com.adi.gestuser.utils;

import com.adi.gestuser.entity.ApiKey;
import com.adi.gestuser.enums.ApikeyRole;
import com.adi.gestuser.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApiKeyDataLoader implements CommandLineRunner {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    public void run(String... args) {
        // API key con ruolo READ
        ApiKey apiKeyRead = new ApiKey();
        apiKeyRead.setApikey("api-key-read");
        apiKeyRead.setExpireDate( LocalDateTime.now().plusDays(30));
        apiKeyRead.setApikeyRole( ApikeyRole.READ );

        // API key con ruolo WRITE
        ApiKey apiKeyWrite = new ApiKey();
        apiKeyWrite.setApikey("api-key-write");
        apiKeyWrite.setExpireDate(LocalDateTime.now().plusDays(30));
        apiKeyWrite.setApikeyRole(ApikeyRole.WRITE);

        apiKeyRepository.save(apiKeyRead);
        apiKeyRepository.save(apiKeyWrite);
    }
}
