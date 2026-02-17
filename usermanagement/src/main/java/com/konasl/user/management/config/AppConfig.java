package com.konasl.user.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * General application configuration for enabling Async and AOP.
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
public class AppConfig {
}
