package com.security.service;

import com.exception.ExpiredJwtException;
import com.security.factory.JwtFactory;
import com.security.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Map;

@Component
public class JwtCookieService implements Serializable {

    private CookieService cookieService;
    private TokenService tokenService;
    private JwtService jwtService;
    private JwtFactory jwtFactory;

    private static final long serialVersionUID = -3301605591108950415L;

    @Value("${app-token-expiration-in-milliseconds}")
    private long expiration;

    @Value("${app-refresh-token-expiration-in-milliseconds}")
    private long refreshExpiration;

    @Value("${app-authorization-cookie-name}")
    private String cookieName;

    @Value("${app-refresh-cookie-name}")
    private String refreshCookieName;

    @Autowired
    public JwtCookieService(CookieService cookieService,
                            TokenService tokenService,
                            JwtService jwtService,
                            JwtFactory jwtFactory) {
        this.cookieService = cookieService;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.jwtFactory = jwtFactory;
    }


    public Map<String, Token> generateAccessJwtFromRefreshJwtInCookie(
            UserDetails userDetails, HttpServletRequest request) {
        Cookie cookie = cookieService.safelyGetCookieByName(refreshCookieName, request.getCookies());
        throwExceptionIfJwtInCookieIsExpired(cookie);
        return generateAccessJwtUsing(userDetails);
    }

    private void throwExceptionIfJwtInCookieIsExpired(Cookie cookie) {
        if (jwtService.jwtIsExpired(cookie.getValue())) {
            throw new ExpiredJwtException("Token has expired, please log in!");
        }
    }

    public Map<String, Token> generateAccessJwtUsing(UserDetails userDetails) {
        return tokenService.generateSingletonTokenMap(userDetails);
    }

    public void attachAccessJwtToCookieUsing(UserDetails userDetails, Device device, HttpServletResponse res) {
        String token = jwtFactory.createJwt(
                device, userDetails, expiration);
        cookieService.createSecureCookie(token, res, expiration, cookieName);
    }

    public void attachRefreshJwtToCookieUsing(UserDetails userDetails, Device device, HttpServletResponse res) {
        String token = jwtFactory.createJwt(
                device, userDetails, refreshExpiration);
        cookieService.createSecureCookie(token, res, refreshExpiration, refreshCookieName);
    }

}

