package com.auth.stateless_auth_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatelessAuthSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatelessAuthSystemApplication.class, args);
	}

}


/*
Request
 ↓
JwtAuthFilter
 ↓
Extract Token
 ↓
JwtService → getUserIdFromToken()
 ↓
UserRepository → load user
 ↓
Create Authentication Object
 ↓
SecurityContextHolder.setAuthentication()
 ↓
Controller executes

 */


/*

 Register endpoint
 Login endpoint
 Access Token (JWT)
 Refresh Token
 Password encryption (BCrypt)
 Spring Security configuration
 JwtAuthFilter (stateless authentication)
 UserDetailsService integration
 MySQL database persistence
 Duplicate email prevention

 */
