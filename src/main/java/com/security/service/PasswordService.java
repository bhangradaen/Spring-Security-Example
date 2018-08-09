package com.security.service;

import com.exception.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void throwExceptionIfEnteredPasswordIsInvalid(
            String passwordInRequest, String hashedPassword) throws InvalidPasswordException {
        if (!passwordEncoder.matches(passwordInRequest, hashedPassword))
            throw new InvalidPasswordException();
    }

}
