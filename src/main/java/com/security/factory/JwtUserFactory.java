package com.security.factory;

import com.security.model.Function;
import com.security.model.JwtUserDetails;
import com.security.model.Role;
import com.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtUserFactory {

    // TODO: Figure out why scopes array isn't printing off

    public JwtUserDetails create(User user) {
        Collection<Role> userRoles = user.getRoles();
        ArrayList<Function> userFunctions = new ArrayList<>();
        for(Role role: userRoles) {
            userFunctions.addAll(role.getFunctions());
        }
        Collection<GrantedAuthority> authorities = (userFunctions.stream()
                .map(function -> new SimpleGrantedAuthority("ROLE_".concat(function.getName())))
                .collect(Collectors.toList()));
        return new JwtUserDetails(user.getUserId(), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getActive(), authorities, user.getHashedPassword());
    }

}