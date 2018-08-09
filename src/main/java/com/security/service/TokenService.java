package com.security.service;

import com.exception.InvalidExpirationTimeException;
import com.security.model.JwtUserDetails;
import com.security.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${app-token-expiration-in-milliseconds}")
    private long expiration;

    public Map<String, Token> generateSingletonTokenMap(UserDetails userDetails) {
        Token token = createTokenModelObject(
                userDetails.getUsername(),
                ((JwtUserDetails)userDetails).getId(),
                expiration,
                userDetails.getAuthorities());
        return Collections.singletonMap("accessToken", token);
    }

    private Token createTokenModelObject(String subject, int userId, long millisUntilExpiration,
                                         Collection<? extends GrantedAuthority> authorities) {
        Claims claims = generateClaimsForSubjectAndAuthorities(subject, authorities);
        Date exp = generateExpirationDateBasedOnExpirationMillis(millisUntilExpiration);
        return new Token(subject, userId, exp, claims.get("scopes", List.class));

    }

    private Claims generateClaimsForSubjectAndAuthorities(
            String subject, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", authorities.stream().map(
                authority -> authority.toString()).collect(Collectors.toList()));
        return claims;
    }

    private Date generateExpirationDateBasedOnExpirationMillis(long millisecondsUntilExpiration) {
        throwExceptionIfExpirationTimeIsNegative(millisecondsUntilExpiration);
        long expirationDateInMilliseconds = System.currentTimeMillis() + millisecondsUntilExpiration;
        return new Date(expirationDateInMilliseconds);
    }

    private void throwExceptionIfExpirationTimeIsNegative(long millisecondsUntilExpiration) {
        if (millisecondsUntilExpiration < 0) {
            throw new InvalidExpirationTimeException("Expiration time must be a non negative number");
        }
    }

}

