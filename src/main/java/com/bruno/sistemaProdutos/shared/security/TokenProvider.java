package com.bruno.sistemaProdutos.shared.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {//é como se fosse um dto para gerar e validar tokens JWT

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.key}")
    private String key;

    // gerar um token
    public String generateToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal(); //pega o usuário autenticado
        return buildToken(user.getUsername());
    } 

    public String buildToken(String username){
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    //assinar o token com a chave secreta
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes()); //gerar a chave de assinatura a partir da string do key
    }

    //validar token
    public boolean isTokenValid(String token){
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

     //obter username do token
     public String getUsernameFromToken(String token){
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token){//claims = payload
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)//vê se o token é válido ou expirou 
                .getPayload();//retorna as claims (payload)
    }

    //extrair informações do token
    public String getUsername(String token){
        return getClaims(token).getSubject();
    }

}
