package com.almina.ecommerce.util;

import com.almina.ecommerce.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public final class AuthPrincipalFactory {

    private AuthPrincipalFactory() {
    }

    public static UserDetails fromUser(User user) {
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
