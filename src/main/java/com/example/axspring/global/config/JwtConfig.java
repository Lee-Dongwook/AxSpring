package com.example.axspring.global.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
        return new RSASSASigner(
                loadPrivateKey(
                        properties.privateKeyPath()
                )
        );
    }

    @Bean
    public RSAPublicKey jwtPublicKey(
            JwtProperties properties
    ) throws Exception {
        return loadPublicKey(
                properties.publicKeyPath()
        );
    }

    private RSAPrivateKey loadPrivateKey(
            String path
    ) throws Exception {
        String pem = Files.readString(Path.of(path));

        String key = pem
                .replace(
                        "-----BEGIN PRIVATE KEY-----",
                        ""
                )
                .replace(
                        "-----END PRIVATE KEY-----",
                        ""
                )
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(key);

        return (RSAPrivateKey)
                KeyFactory
                        .getInstance("RSA")
                        .generatePrivate(
                                new PKCS8EncodedKeySpec(decoded)
                        );
    }

    private RSAPublicKey loadPublicKey(
            String path
    ) throws Exception {
        String pem = Files.readString(Path.of(path));

        String key = pem
                .replace(
                        "-----BEGIN PUBLIC KEY-----",
                        ""
                )
                .replace(
                        "-----END PUBLIC KEY-----",
                        ""
                )
                .replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(key);

        return (RSAPublicKey)
                KeyFactory
                        .getInstance("RSA")
                        .generatePublic(
                                new X509EncodedKeySpec(decoded)
                        );
    }
}
