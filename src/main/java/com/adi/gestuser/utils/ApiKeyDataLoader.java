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
        ApiKey apiKeyRead = createApiKeyWith30DaysExpire( "api-key-read", ApikeyRole.READ );
        ApiKey apiKeyWrite = createApiKeyWith30DaysExpire( "api-key-write", ApikeyRole.WRITE );

        // Verifica che l'apikey non esista già
        saveAPikeyIfNotExists(apiKeyRead);
        saveAPikeyIfNotExists(apiKeyWrite);
    }

    private ApiKey createApiKeyWith30DaysExpire(String apikey, ApikeyRole apikeyRole) {
        ApiKey apiKey = new ApiKey();
        apiKey.setApikey(apikey);
        apiKey.setExpireDate(LocalDateTime.now().plusDays(30));
        apiKey.setApikeyRole(apikeyRole);
        return apiKey;
    }

    private void saveAPikeyIfNotExists(ApiKey apiKey) {
        ApiKey apiKeyFound = apiKeyRepository.findByApikey(apiKey.getApikey());

        if (apiKeyFound == null) {
            apiKeyRepository.save(apiKey);
        }
    }
}
