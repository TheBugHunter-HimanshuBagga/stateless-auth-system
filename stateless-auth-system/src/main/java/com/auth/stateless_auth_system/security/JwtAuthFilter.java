package com.auth.stateless_auth_system.security;

import com.auth.stateless_auth_system.Entity.User;
import com.auth.stateless_auth_system.Repository.UserRepository;
import com.auth.stateless_auth_system.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

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
public class JwtAuthFilter extends OncePerRequestFilter { // creats this class as bean , manage it in spring container , allow it to be injected into spring container
    // OncePerRequestFilter - the filter runs only once per HTTP request
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Read Authorization header
        try {
            final String authHeader = request.getHeader("Authorization"); // Authorization: Bearer eyJhbGciOiJIUzI1NiIs... {it should always be starting from Bearer }
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // Header exists , should be starting from Bearer
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.split("Bearer ")[1]; // extract JWT from the authHeader
            Long userId = jwtService.getUserIdFromToken(token); //  validate jwt token JWT → decode → verify signature → read subject
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findById(userId).orElseThrow(
                        () -> new UsernameNotFoundException("User not Found with Id: " + userId)
                );
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request) // remote Ip address and session ID`
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); // From now spring believes the user as a true user and is stored in spring context
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e){
            handlerExceptionResolver.resolveException(request , response , null , e);
        }
    }


}

/*
Read Authorization header

Extract token

Get userId from token

Load user from DB

Set authentication in Spring context
 */


/*
SecurityContextHolder
        ↓
SecurityContext
        ↓
Authentication
 */

/*
UsernamePasswordAuthenticationToken

It is a Spring Security Authentication object.
Spring Security represents a logged-in user using an object called Authentication.

This object stores:
Who the user is
Their credentials
Their roles/permissions

So it represents:
Authenticated User Session (for this request)
 */

/*
What is handlerExceptionResolver

It is a Spring component that handles exceptions globally.

Spring normally handles exceptions through:

@ControllerAdvice
@ExceptionHandler

But filters run before controllers, so those handlers don't automatically work.

HandlerExceptionResolver helps us forward the exception to Spring's global exception handler.
Client Request
      ↓
Filters (JwtAuthFilter) -> handlerExceptionResolver
      ↓
Spring Security
      ↓
DispatcherServlet
      ↓
Controllers
      ↓
@ControllerAdvice / @ExceptionHandler
 */



/*
final flow

Request
  ↓
Read Authorization header
  ↓
Extract JWT token
  ↓
Decode + verify JWT
  ↓
Extract userId
  ↓
Load user from database
  ↓
Create Authentication object
  ↓
Store in SecurityContextHolder
  ↓
Controller executes
 */
