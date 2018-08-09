package com.security.controller;

import com.exception.ExpiredJwtException;
import com.exception.InvalidJwtException;
import com.security.model.Token;
import com.security.service.CookieService;
import com.security.service.JwtCookieService;
import com.security.service.JwtService;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/user/refresh")
public class RefreshTokenController {

    @Value("${app-refresh-cookie-name}")
    private String refreshCookieName;

    private final JwtCookieService jwtCookieService;
    private final CookieService cookieService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public RefreshTokenController(JwtCookieService jwtCookieService,
                                  CookieService cookieService,
                                  UserService userService,
                                  JwtService jwtService) {
        this.jwtCookieService = jwtCookieService;
        this.cookieService = cookieService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<String, Token>> refreshAccessToken(
            HttpServletRequest request, HttpServletResponse response, Device device) {
        try {
            Map<String, Token> accessToken = generateAccessTokenFromRequest(request, response, device);
            return new ResponseEntity<>(accessToken, HttpStatus.OK);
        } catch (InvalidJwtException e) {
            throw new ExpiredJwtException("Refresh token not found");
        } catch (Exception e) {
            throw new InvalidJwtException("There was an error refreshing the token");
        }
    }

    private Map<String, Token> generateAccessTokenFromRequest(
            HttpServletRequest request, HttpServletResponse response, Device device) {
        Cookie cookie = cookieService.safelyGetCookieByName(refreshCookieName, request.getCookies());
        String username = jwtService.getUsernameFromJwt(cookie.getValue());
        final UserDetails userDetails = userService.loadUserByUsername(username);
        jwtCookieService.attachAccessJwtToCookieUsing(userDetails, device, response);
        return jwtCookieService.generateAccessJwtFromRefreshJwtInCookie(userDetails, request);
    }

}

