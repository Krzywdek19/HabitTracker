package com.krzywdek19.auth_service.service;

import com.krzywdek19.auth_service.config.RsaProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;


@Service
public class JwtService {
    private final SignatureAlgorithm algorithm;
    private final KeyPair keyPair;


    JwtService(KeyPair keys) {
        keyPair = keys;
        algorithm = SignatureAlgorithm.RS256;
    }
}
