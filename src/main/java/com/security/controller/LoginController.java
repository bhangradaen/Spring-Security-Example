package com.security.controller;

import com.security.model.LoginRequest;
import com.security.model.Token;
import com.security.service.JwtCookieService;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class LoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtCookieService jwtCookieService;

    @Autowired
    public LoginController(UserService userService, AuthenticationManager authenticationManager,
                           JwtCookieService jwtCookieService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtCookieService = jwtCookieService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Token>> login(
            @RequestBody LoginRequest loginRequest, Device device, HttpServletResponse response) {
        final UserDetails userDetails = userService.loadUserFromLogin(loginRequest);
        setAuthenticationInSecurityContext(loginRequest, userDetails);
        Map<String, Token> accessTokenAsSingletonMap =
                jwtCookieService.generateAccessJwtUsing(userDetails);
        jwtCookieService.attachAccessJwtToCookieUsing(userDetails, device, response);
        jwtCookieService.attachRefreshJwtToCookieUsing(userDetails, device, response);
        return new ResponseEntity<>(accessTokenAsSingletonMap, HttpStatus.OK);
    }

    private void setAuthenticationInSecurityContext(LoginRequest loginRequest, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), userDetails.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

