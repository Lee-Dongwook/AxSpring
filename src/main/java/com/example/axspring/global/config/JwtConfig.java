package com.example.axspring.global.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.axspring.auth.adapter.out.token.JwtProperties;
import com.nimbusds.jose.crypto.RSASSASigner;

@Configuration
public class JwtConfig {
    
    @Bean
    public RSASSASigner jwtSigner(
        JwtProperties properties
    ) throws Exception {
        RSAPrivateKey privateKey =
                loadPrivateKey(properties.privateKeyPath());
        
        return new RSASSASigner(privateKey);
    }

    private RSAPrivateKey loadPrivateKey(
        String path
    ) throws Exception {
        String pem = Files.readString(Path.of(path));

        String privateKey = pem
                .replace(
                        "-----BEGIN PRIVATE KEY-----",
                        ""
                )
                .replace(
                        "-----END PRIVATE KEY-----",
                        ""
                )
                .replaceAll("\\s", "");

        byte[] decoded = Base64
                .getDecoder()
                .decode(privateKey);
        
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey)
                keyFactory.generatePrivate(spec);
    }
}
