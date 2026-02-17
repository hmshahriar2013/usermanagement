package com.konasl.user.management.domain.access.repository;

import com.konasl.user.management.domain.access.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUserId(UUID userId);
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
