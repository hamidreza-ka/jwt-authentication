package com.my.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.authentication.registration.RegistrationRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {



    private AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            RegistrationRequest registrationRequest = new ObjectMapper().readValue(request.getInputStream(),
                    RegistrationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    registrationRequest.getEmail(),
                    registrationRequest.getPassword()
            );

            Authentication authenticate = authenticationManager.authenticate(authentication);
            return authenticate;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String secretKey = "kljasdlkjKLAJSDKLJADkjasdklad-asd;lkqwed[0qokljasdlkjKLAJSDKLJADkjasdklad-asd;lkqwed[0qo";
        String token = Jwts.builder()
                .setSubject(authResult.getCredentials().toString())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDateTime.now().plusMinutes(15).toLocalDate()))
                .setId(String.valueOf(new Random().nextInt(9999)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        response.addHeader("Authorization", "Bearer " + token);

    }
}
