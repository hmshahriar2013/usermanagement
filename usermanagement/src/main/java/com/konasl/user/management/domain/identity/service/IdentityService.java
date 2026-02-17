package com.konasl.user.management.domain.identity.service;

import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.audit.annotation.Auditable;
import com.konasl.user.management.domain.audit.model.AuditEventType;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.repository.UserRepository;
import com.konasl.user.management.exception.ErrorCode;
import com.konasl.user.management.exception.IdentityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserRepository userRepository;

    @Transactional
    @Auditable(eventType = AuditEventType.USER_CREATED)
    public User createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IdentityException(ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT, "Username: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IdentityException(ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT, "Email: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.PENDING)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public List<User> createUsersBulk(List<UserRequest> requests) {
        return requests.stream()
                .map(this::createUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(UUID userId, UserRequest request) {
        User user = findById(userId);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        // Email and Username updates might need more strict handling/validation in real projects
        
        return userRepository.save(user);
    }

    @Transactional
    @Auditable(eventType = AuditEventType.USER_ACTIVATED)
    public void updateStatus(UUID userId, UserStatus status) {
        User user = findById(userId);
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> IdentityException.userNotFound(username));
    }

    @Transactional(readOnly = true)
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> IdentityException.userNotFound(userId.toString()));
    }
}
