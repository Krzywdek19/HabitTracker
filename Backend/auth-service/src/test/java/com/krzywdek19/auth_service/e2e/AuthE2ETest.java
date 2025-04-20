package com.krzywdek19.auth_service.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.repository.UserRepository;
import com.krzywdek19.auth_service.response.AuthResponseDto;
import com.krzywdek19.auth_service.service.RedisTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTokenService redisTokenService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void fullFlowShouldWorkCorrectly() throws Exception {
        //SIGN UP
        SignUpDto signUpDto = new SignUpDto("test","test@test.com","password");
        mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk());

        //SIGN IN
        SignInDto signInDto = new SignInDto("test@test.com","password");
        MvcResult signInResult = mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto tokens = objectMapper.readValue(signInResult.getResponse().getContentAsString(), AuthResponseDto.class);
        assertNotNull(tokens.accessToken());
        assertNotNull(tokens.refreshToken());

        //REFRESH TOKEN
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(
                userRepository.findByEmail("test@test.com").get().getId(), tokens.refreshToken()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isOk())
                .andReturn();

        String newToken = refreshResult.getResponse().getContentAsString();
        assertNotNull(newToken);
    }
}
