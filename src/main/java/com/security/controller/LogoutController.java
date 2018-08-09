package com.security.controller;

import com.security.service.CookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class LogoutController {

    @Value("${app-authorization-cookie-name}")
    private String cookieName;

    @Value("${app-refresh-cookie-name}")
    private String refreshCookieName;

    private final CookieService cookieService;

    @Autowired
    public LogoutController(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(HttpServletResponse response) {
        nullifyCookie(response, cookieName);
        nullifyCookie(response, refreshCookieName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void nullifyCookie(HttpServletResponse response, String cookieName) {
        String nullToken = null;
        int expirationTimeRequiredToNullifyCookie = 0;
        cookieService.createSecureCookie(
                nullToken, response, expirationTimeRequiredToNullifyCookie, cookieName);
    }

}
