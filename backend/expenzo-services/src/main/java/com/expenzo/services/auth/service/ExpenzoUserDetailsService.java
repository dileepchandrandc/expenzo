package com.expenzo.services.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.expenzo.services.auth.repository.ExpenzoUserRepository;
import com.expenzo.services.auth.repository.model.ExpenzoUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenzoUserDetailsService implements UserDetailsService {

    private final ExpenzoUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        ExpenzoUser user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return User.builder()
            .username(user.getId())
            .password(user.getPassword())
            .build();
    }

    /**
     * Lookup by email — used only during login flow where the user provides email.
     */
    public UserDetails loadUserByEmail(String email) {
        ExpenzoUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
            .username(user.getId())
            .password(user.getPassword())
            .build();
    }
    
}
