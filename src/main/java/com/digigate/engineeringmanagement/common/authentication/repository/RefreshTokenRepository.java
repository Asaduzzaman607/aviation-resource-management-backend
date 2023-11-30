package com.digigate.engineeringmanagement.common.authentication.repository;

import com.digigate.engineeringmanagement.common.authentication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long id);

    Optional<RefreshToken> findByToken(String token);

    Integer deleteByUserId(Long userId);

    Optional<RefreshToken> findByUserId(Long userId);
}
