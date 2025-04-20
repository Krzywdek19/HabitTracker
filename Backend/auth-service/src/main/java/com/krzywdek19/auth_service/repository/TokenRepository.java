package com.krzywdek19.auth_service.repository;

import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken,String> {
    boolean existsByUser(User user);

    Optional<VerificationToken> findByUser(User user);

    void deleteByUser(User user);
}
