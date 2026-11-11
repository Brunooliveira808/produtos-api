package com.bruno.sistemaProdutos.shared.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bruno.sistemaProdutos.auth.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{


    private final TokenProvider tokenProvider;
    //private final UserDetailsService userDetailsService; // serviço para carregar os detalhes do usuário a partir do username extraído do token
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            
            String token = authorizationHeader.substring(7); //remover "Bearer " do início do token

            if(tokenProvider.isTokenValid(token)){
                String username = tokenProvider.getUsernameFromToken(token);

                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);//carregar os detalhes do usuário a partir do username extraído do token

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()//criar um token de autenticação do Spring Security com os detalhes do usuário e suas autoridades
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);//definir o token de autenticação no contexto de segurança do Spring Security para que o usuário seja autenticado para a requisição atual

            }
        } 
        //tratar erro de token inválido ou expirado
        filterChain.doFilter(request, response);

    }
}

