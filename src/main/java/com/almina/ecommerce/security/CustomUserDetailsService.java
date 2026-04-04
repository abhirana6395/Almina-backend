package com.almina.ecommerce.security;

import com.almina.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(this::toUserDetails)
                .orElse(null);
    }

    public UserDetails loadUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toUserDetails)
                .orElse(null);
    }

    private UserDetails toUserDetails(com.almina.ecommerce.entity.User user) {
        return User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authorities(java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .disabled(!user.isActive())
                .build();
    }
}
