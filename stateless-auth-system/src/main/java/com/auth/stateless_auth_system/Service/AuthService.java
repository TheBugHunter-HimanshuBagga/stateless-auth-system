package com.auth.stateless_auth_system.Service;

import com.auth.stateless_auth_system.DTO.AuthRequest;
import com.auth.stateless_auth_system.DTO.AuthResponse;
import com.auth.stateless_auth_system.Entity.Enum.Role;
import com.auth.stateless_auth_system.Entity.User;
import com.auth.stateless_auth_system.Repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(AuthRequest request){
        User user = User.builder() // creating a user object to store in the database
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // We send token in response since just after registering the user get login directly
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse login(AuthRequest request){
        authenticationManager.authenticate( // verify Login Logic AuthenticationManager -> DaoAuthenticationManager -> CustomUserDetailsService -> UserRepository -> DataBase
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User do not exists with email: " + request.getEmail())
        );// I got the user since i need to generate the tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
/*
Client Login Request
        ↓
AuthController
        ↓
AuthService.login()
        ↓
AuthenticationManager.authenticate()
        ↓
DaoAuthenticationProvider
        ↓
UserDetailsService
        ↓
Database
        ↓
Password Verified
        ↓
JWT Tokens Generated
        ↓
Response Sent
 */
