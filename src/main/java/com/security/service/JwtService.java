package com.security.service;

import com.security.model.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app-secret}")
    private String secret;

    private Clock clock = DefaultClock.INSTANCE;

    public String getUsernameFromJwt(String token) {
        return getClaimFromJwt(token, Claims::getSubject);
    }

    private <T> T getClaimFromJwt(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromJwt(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromJwt(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean jwtIsExpired(String token) {
        return getExpirationDateFromJwt(token).before(clock.now());
    }

    private Date getExpirationDateFromJwt(String token) {
        return getClaimFromJwt(token, Claims::getExpiration);
    }

    public Boolean validateJwt(String token, UserDetails userDetails) {
        JwtUserDetails user = (JwtUserDetails) userDetails;
        final String username = getUsernameFromJwt(token);
        return (username.equals(user.getUsername()) && !jwtIsExpired(token));
    }

}

