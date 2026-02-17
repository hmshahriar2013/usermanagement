package com.konasl.user.management.security;

import com.konasl.user.management.domain.access.service.AccessControlService;
import com.konasl.user.management.domain.auth.model.Credential;
import com.konasl.user.management.domain.auth.repository.CredentialRepository;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IdentityService identityService;
    private final CredentialRepository credentialRepository;
    private final AccessControlService accessControlService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch user from Identity context (ums_core)
        User user = identityService.findByUsername(username);

        // 2. Fetch credentials from Auth context (ums_auth)
        Credential credential = credentialRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Credentials not found for user: " + username));

        // 3. Map to Spring Security UserDetails with Authorities from Access Control context
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                credential.getPasswordHash(),
                accessControlService.getUserAuthorities(user.getUserId())
        );
    }
}
