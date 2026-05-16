package com.example.GachonHack.domain.user.repository;

import com.example.GachonHack.domain.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByProviderAndProviderUid(String provider, String providerUid);
}
