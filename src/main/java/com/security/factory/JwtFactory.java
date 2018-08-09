package com.security.factory;

import com.device.ClientDeviceFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.util.DateTimeUtil.generateExpirationDateForTimeInMilliseconds;

@Component
public class JwtFactory {

    @Value("${app-secret}")
    private String secret;

    @Value("${app-token-issuer}")
    private String tokenIssuer;

    private final ClientDeviceFactory clientDeviceFactory;

    @Autowired
    public JwtFactory(ClientDeviceFactory clientDeviceFactory) {
        this.clientDeviceFactory = clientDeviceFactory;
    }

    public String createJwt(Device device, UserDetails userDetails, long millisecondsUntilExpiration) {
        Date expirationDate = generateExpirationDateForTimeInMilliseconds(millisecondsUntilExpiration);
        return buildCompactJwtStringFor(
                UUID.randomUUID().toString(),
                new Date(),
                expirationDate,
                userDetails.getUsername(),
                clientDeviceFactory.createClientDeviceFor(device).getAudience(),
                generateUserClaims(userDetails.getUsername(), userDetails.getAuthorities(), expirationDate));
    }

    private Claims generateUserClaims(
            String subject, Collection<? extends GrantedAuthority> authorities, Date expirationDateTime) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", authorities.stream().map(s -> s.toString()).collect(Collectors.toList()));
        claims.setExpiration(expirationDateTime);
        return claims;
    }

    private String buildCompactJwtStringFor(
            String uuidAsString, Date now, Date expirationDateTime, String subject, String audience, Claims claims) {
        return Jwts.builder()
                .setId(uuidAsString)
                .setIssuedAt(now)
                .setExpiration(expirationDateTime)
                .setSubject(subject)
                .setIssuer(tokenIssuer)
                .setAudience(audience)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

}