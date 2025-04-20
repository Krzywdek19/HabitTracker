package com.krzywdek19.auth_service.integration;

import com.krzywdek19.auth_service.model.Role;
import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.repository.UserRepository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        String email = "test@test.com";
        String password = "password";
        User user = User.
                builder()
                .email(email)
                .username("test")
                .password(password)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        assertTrue(optionalUser.isPresent());
        assertEquals(password, optionalUser.get().getPassword());
    }
}
