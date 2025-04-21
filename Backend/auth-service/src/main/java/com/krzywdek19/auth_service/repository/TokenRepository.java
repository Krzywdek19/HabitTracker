package com.krzywdek19.auth_service.repository;

import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken,String> {
    boolean existsByUser(User user);
    Optional<VerificationToken> findByUser(User user);
    @Query("SELECT vt FROM VerificationToken vt JOIN FETCH vt.user u WHERE u.id = :userId")
    Optional<VerificationToken> findByUserId(@Param("userId") Long userId);
    void deleteByUser(User user);
    Optional<VerificationToken> findByToken(String token);
}
