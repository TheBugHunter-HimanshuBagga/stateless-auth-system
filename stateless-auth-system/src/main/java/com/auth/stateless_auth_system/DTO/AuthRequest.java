package com.auth.stateless_auth_system.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}


/*

{
  "email": "test@gmail.com",
  "password": "1234"
}

Spring automatically maps this into: AuthRequest request inside controller


FLOW -> FrontEnd sends JSON - spring converts in into flow AuthRequest - then we authenticate using authenticationManager
 */