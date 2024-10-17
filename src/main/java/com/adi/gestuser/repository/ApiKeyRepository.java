package com.adi.gestuser.repository;

import com.adi.gestuser.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {

    ApiKey findByApikey(String apikey);
}
