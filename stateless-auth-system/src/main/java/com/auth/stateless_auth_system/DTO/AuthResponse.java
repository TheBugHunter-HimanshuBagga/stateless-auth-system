package com.auth.stateless_auth_system.DTO;

import lombok.Builder;
import lombok.Data;

/*
Token will be returned from here
 */
@Data
@Builder
public class AuthResponse {
    private String token;
}
