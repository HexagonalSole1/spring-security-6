package org.devquality.safetyauthservice.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.persistence.repositories.IUserRepository;
import org.devquality.safetyauthservice.security.entities.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("🔍 Loading user details for: {}", username);

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    log.warn("❌ User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        if (!user.getActive()) {
            log.warn("❌ User account is deactivated: {}", username);
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        log.debug("✅ User details loaded successfully for: {}", username);
        return new UserDetailsImpl(user);
    }
}