package com.auth.stateless_auth_system.security;

import com.auth.stateless_auth_system.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/*
This is what makes stateless auth work.
It runs before every request
👉 Check if request has JWT
👉 Validate it
👉 Tell Spring → “this user is authenticated”

COMPONENT
Because we want Spring to:

👉 Create this object automatically
👉 Manage it
👉 Inject it into SecurityConfig
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
}
