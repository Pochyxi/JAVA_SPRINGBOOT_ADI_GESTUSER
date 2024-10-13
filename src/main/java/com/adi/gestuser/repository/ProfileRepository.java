package com.adi.gestuser.repository;

import com.adi.gestuser.entity.Profile;
import com.adi.gestuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, User> {

    Profile findByUserId( Long userId);
}
