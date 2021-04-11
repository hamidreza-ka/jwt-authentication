package com.my.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {

    // String SECRET_KEY = "kljasdlkjKLAJSDKLJ4243ADkjasdkladasdlkqwed7520qokljasdlkj694964KLAJSDKLJADkjasdkladaslkqwe0qo";
    KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);

    public String generateToken(UserDetails userDetails, boolean refreshToken) {
        //  Map<String, Object> claims = new HashMap<>();
        if (refreshToken)
            return createRefreshToken(userDetails.getUsername());
        return createToken(userDetails.getUsername());
    }

    private String createToken(String subject) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "access_token");
        return Jwts.builder()
                .setClaims(map)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 10)))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

    }

    private String createRefreshToken(String subject) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "refresh_token");
        return Jwts.builder()
                .setClaims(map)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 150)))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();


    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractGrantType(String token) {
        return extractAllClaim(token).get("grant_type", String.class);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    private Boolean isTokenExpiration(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpiration(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

}
