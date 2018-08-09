package com.security.filter;

import com.exception.ExpiredJwtException;
import com.exception.InvalidJwtException;
import com.exception.MissingJwtException;
import com.security.service.CookieService;
import com.security.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CookieService cookieService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${app-authorization-cookie-name}")
    private String cookieName;

    @Value("${authentication-paths}")
    private String pathsToSkip;

    public JwtAuthenticationFilter(CookieService cookieService,
                                   JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.cookieService = cookieService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            filter(request);
            chain.doFilter(request, response);
        } catch (MissingJwtException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            throw new InvalidJwtException("Access token not found");
        }
    }

    private void filter(HttpServletRequest request) {
        List<String> skipURIsList = generateListOfUriPathsToSkip();
        if (requestDoesNotHoldUriInSkipList(request, skipURIsList)) {
            authenticateUserViaServletRequest(request);
        }
    }

    private List<String> generateListOfUriPathsToSkip() {
        List<String> skipURIsList = null;
        if (thereArePathsToSkip())
            skipURIsList = Arrays.asList(pathsToSkip.split(","));
        return skipURIsList;
    }

    private boolean thereArePathsToSkip() {
        return pathsToSkip != null;
    }

    private boolean requestDoesNotHoldUriInSkipList(HttpServletRequest request, List<String> skipURIsList) {
        return !skipURIsList.stream().anyMatch(s -> request.getRequestURI().contains(s));
    }

    private void authenticateUserViaServletRequest(HttpServletRequest request) {
        String authToken = this.getJwtFromRequest(request);
        String username = getUsernameFromJwt(authToken);
        logger.info("Checking authentication for user " + username);
        loadUserDetailsAndSetAuthentication(username, authToken, request);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie cookie = cookieService.safelyGetCookieByName(cookieName, cookies);
            authToken = cookie.getValue();
        }
        return authToken;
    }

    private String getUsernameFromJwt(String authToken) {
        if (authToken != null) {
            return extractUsernameFromJwt(authToken);
        } else {
            throw new MissingJwtException("Authorization cookie not found");
        }
    }

    private String extractUsernameFromJwt(String authToken) {
        String username = null;
        try {
            username = jwtService.getUsernameFromJwt(authToken);
        } catch (IllegalArgumentException e) {
            logger.error("Username not in token", e);
        } catch (ExpiredJwtException e) {
            logger.info("Token has expired", e);
            throw new ExpiredJwtException("Token has expired");
        } catch (SignatureException e) {
            logger.error("Jwt Signature is invalid", e);
            throw new InvalidJwtException("Jwt Signature is invalid");
        } catch (MalformedJwtException e) {
            logger.error("Jwt is malformed", e);
            throw new InvalidJwtException("Jwt is malformed");
        }
        return username;
    }

    private void loadUserDetailsAndSetAuthentication(String username, String authToken, HttpServletRequest request) {
        if (authenticationIsNull()) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (tokenIsValidAccordingToUserDetails(authToken, userDetails)) {
                setAuthenticationForUserRequest(userDetails, request);
            }
        }
    }

    private boolean authenticationIsNull() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private boolean tokenIsValidAccordingToUserDetails(String authToken, UserDetails userDetails) {
        return authToken != null && jwtService.validateJwt(authToken, userDetails);
    }

    private void setAuthenticationForUserRequest(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
