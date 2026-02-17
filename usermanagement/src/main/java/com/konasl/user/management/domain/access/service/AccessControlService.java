package com.konasl.user.management.domain.access.service;

import com.konasl.user.management.domain.access.model.Permission;
import com.konasl.user.management.domain.access.model.Role;
import com.konasl.user.management.domain.access.model.RolePermission;
import com.konasl.user.management.domain.access.model.UserRole;
import com.konasl.user.management.domain.access.repository.PermissionRepository;
import com.konasl.user.management.domain.access.repository.RolePermissionRepository;
import com.konasl.user.management.domain.access.repository.RoleRepository;
import com.konasl.user.management.domain.access.repository.UserRoleRepository;
import com.konasl.user.management.exception.ErrorCode;
import com.konasl.user.management.exception.IdentityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional
    @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.ROLE_CREATED)
    public Role createRole(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new IdentityException(ErrorCode.INVALID_INPUT, HttpStatus.CONFLICT, "Role already exists: " + name);
        }
        Role role = Role.builder()
                .name(name)
                .description(description)
                .build();
        return roleRepository.save(role);
    }

    @Transactional
    @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.PERMISSION_CREATED)
    public Permission createPermission(String name, String description) {
        if (permissionRepository.findByName(name).isPresent()) {
            throw new IdentityException(ErrorCode.INVALID_INPUT, HttpStatus.CONFLICT, "Permission already exists: " + name);
        }
        Permission permission = Permission.builder()
                .name(name)
                .description(description)
                .build();
        return permissionRepository.save(permission);
    }

    @Transactional
    @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.ROLE_ASSIGNED_TO_USER)
    public void assignRoleToUser(UUID userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IdentityException(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Role not found: " + roleName));
        
        UserRole userRole = UserRole.builder()
                .userId(userId)
                .roleId(role.getRoleId())
                .build();
        userRoleRepository.save(userRole);
    }

    @Transactional
    @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.PERMISSION_ADDED_TO_ROLE)
    public void addPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IdentityException(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Role not found: " + roleName));
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IdentityException(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, "Permission not found: " + permissionName));
        
        RolePermission rolePermission = RolePermission.builder()
                .roleId(role.getRoleId())
                .permissionId(permission.getPermissionId())
                .build();
        rolePermissionRepository.save(rolePermission);
    }

    @Transactional(readOnly = true)
    public Collection<? extends GrantedAuthority> getUserAuthorities(UUID userId) {
        Set<String> authorities = new HashSet<>();
        
        // 1. Get Roles
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        for (UserRole userRole : userRoles) {
            roleRepository.findById(userRole.getRoleId()).ifPresent(role -> {
                authorities.add(role.getName()); // e.g., ROLE_ADMIN
                
                // 2. Get Permissions for each role
                List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getRoleId());
                for (RolePermission rp : rolePermissions) {
                    permissionRepository.findById(rp.getPermissionId())
                            .ifPresent(p -> authorities.add(p.getName())); // e.g., identity:view
                }
            });
        }
        
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
