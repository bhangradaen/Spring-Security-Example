package com.security.service;

import com.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static java.lang.Math.toIntExact;

@Service
public class CookieService {

    @Value("${app-cookie-domain-name}")
    private String domainName;

    @Value("${app-cookie-secure}")
    private String secureCookie;

    public Cookie safelyGetCookieByName(String name, Cookie[] cookies) throws InvalidJwtException {
        try {
            return getCookieByName(name, cookies);
        } catch (NullPointerException e) {
            throw new InvalidJwtException("Cookie not found: " + name);
        }
    }

    private Cookie getCookieByName(String name, Cookie[] cookies) {
        for (Cookie cookie: cookies) {
            if (name.equals(cookie.getName()))
                return cookie;
        }
        throw new NullPointerException();
    }

    public void createSecureCookie(String token, HttpServletResponse response, long expiration, String cookieName) {
        Cookie cookie = createCookie(token, expiration, cookieName);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String token, long expirationTimeAsLong, String cookieName) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(toIntExact(expirationTimeAsLong));
        cookie.setDomain(domainName);
        cookie.setSecure(Boolean.valueOf(secureCookie));
        cookie.setPath("/");
        return cookie;
    }

}
