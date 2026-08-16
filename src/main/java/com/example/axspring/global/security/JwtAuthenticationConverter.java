package com.example.axspring.global.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    
    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        String role = jwt.getClaimAsString("role");

        Collection<GrantedAuthority> authorities = 
            role == null ? List.of() : List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
                );
        
        return new JwtAuthenticationToken(
            jwt,
            authorities,
            jwt.getSubject()
        );
    }
}
