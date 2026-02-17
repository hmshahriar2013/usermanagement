package com.konasl.user.management.integration;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class AccessTestController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Admin Content";
    }

    @GetMapping("/perm-read")
    @PreAuthorize("hasAuthority('user:read')")
    public String permRead() {
        return "Readable Data";
    }
}
