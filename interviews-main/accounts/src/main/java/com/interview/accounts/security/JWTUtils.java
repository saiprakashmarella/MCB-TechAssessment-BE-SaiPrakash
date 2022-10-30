package com.interview.accounts.security;

import com.interview.accounts.config.SpringSecurityConfig;
import com.interview.accounts.service.AccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTUtils {

    private final SpringSecurityConfig springSecurityConfig;

    private final AccountService accountService;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("Inside JWTUtils.extractClaim");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        log.info("Inside JWTUtils.extractAllClaims");
        return Jwts.parser().setSigningKey(springSecurityConfig.getSecret()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        log.info("Inside JWTUtils.isTokenExpired");
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        log.info("Inside JWTUtils.generateToken");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.info("Inside JWTUtils.createToken");
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + springSecurityConfig.getTokenExpiry()))
                .signWith(SignatureAlgorithm.HS256, springSecurityConfig.getSecret()).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("Inside JWTUtils.validateToken");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && accountService.checkUserValidity(userDetails.getUsername(),userDetails.getPassword()));
    }
}
