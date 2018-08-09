package com.user.service;

import com.exception.InvalidPasswordException;
import com.security.factory.JwtUserFactory;
import com.security.model.JwtUserDetails;
import com.security.model.LoginRequest;
import com.security.service.PasswordService;
import com.user.model.User;
import com.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordService passwordService;
    private JwtUserFactory jwtUserFactory;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordService passwordService,
                       JwtUserFactory jwtUserFactory) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtUserFactory = jwtUserFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        throwExceptionIfUserDoesNotExist(user);
        return jwtUserFactory.create(user);
    }

    public UserDetails loadUserFromLogin(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        User user = userRepository.findByUsername(username);
        throwExceptionIfUserDoesNotExist(user);
        return createUserIfLoginPasswordMatchesUserPassword(loginRequest, user);
    }

    private void throwExceptionIfUserDoesNotExist(User user) {
        if (user == null) {
            throw new UsernameNotFoundException("INVALID CREDENTIALS");
        }
    }

    private JwtUserDetails createUserIfLoginPasswordMatchesUserPassword(
            LoginRequest loginRequest, User user) throws InvalidPasswordException {
        String enteredPassword = loginRequest.getPassword();
        String storedHashedPassword = user.getHashedPassword();
        passwordService.throwExceptionIfEnteredPasswordIsInvalid(
                enteredPassword, storedHashedPassword);
        return jwtUserFactory.create(user);
    }

}