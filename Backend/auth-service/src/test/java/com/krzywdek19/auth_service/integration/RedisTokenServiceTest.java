package com.krzywdek19.auth_service.integration;

import com.krzywdek19.auth_service.service.RedisTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureDataRedis
public class RedisTokenServiceTest {

    @Autowired
    private RedisTokenService redisTokenService;

    @Test
    void shouldSaveAndRetrieveToken() {
        String userID = "123";
        String token = "refresh_token_value";

        redisTokenService.saveRefreshToken(userID,token,10000L);

        String retrieved = redisTokenService.getRefreshToken(userID);

        assertEquals(token,retrieved);
    }
}
