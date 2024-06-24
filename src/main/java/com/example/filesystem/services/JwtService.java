package com.example.filesystem.services;

import com.example.filesystem.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    private static final String SECRET = "dHJhdmVscmVwZWF0b2lsc2xlZXBnZW50bGVlZmZvcnRodW5kcmVkcHJvdGVjdGlvbm0=";
    private static final long EXPIRATION_ADD_ON = 1000*60*30;
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        return Jwts.builder()
                .claims(claims)
                .subject(user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_ADD_ON))
                .signWith(getSignKey())
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token){

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){

        return extractClaim(token,Claims::getExpiration);
    }

    public String extractEmail(String token){
        return extractClaim(token,claims -> claims.get("email",String.class));
    }


    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token){

        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String userName){
        final String extractedUserName = extractUserName(token);
        return (extractedUserName.equals(userName) && !isTokenExpired(token));
    }
}
