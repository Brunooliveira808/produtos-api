package com.bruno.sistemaProdutos.service;

import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bruno.sistemaProdutos.config.TokenProvider;
import com.bruno.sistemaProdutos.dto.LoginRequestDto;
import com.bruno.sistemaProdutos.dto.RegisterRequestDto;
import com.bruno.sistemaProdutos.dto.TokenResponseDto;
import com.bruno.sistemaProdutos.entity.Roles;
import com.bruno.sistemaProdutos.entity.Usuario;
import com.bruno.sistemaProdutos.enums.Role;
import com.bruno.sistemaProdutos.repository.RolesRepository;
import com.bruno.sistemaProdutos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolesRepository;  
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private Long expiration;
    

    public ResponseEntity<?> register(RegisterRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
            .orElse(null);

        if (usuario != null) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Este e-mail já está cadastrado.");
        }


        Roles role = rolesRepository.findByNome(Role.ROLE_BASIC.name())
            .orElseGet(() -> rolesRepository.save(Roles.builder()
                .nome(Role.ROLE_BASIC.name())
                .build()));

        usuarioRepository.save(Usuario.builder()
                .email(dto.email())
                .roles(Set.of(role))
                .password(passwordEncoder.encode(dto.password()))
                .build());



        passwordEncoder.matches(dto.password(), "3425252533352535252");//testar o hash da senha para garantir que o passwordEncoder está funcionando corretamente, mesmo que a senha seja diferente do hash fornecido. Isso é útil para verificar se o passwordEncoder está configurado corretamente e pode ser usado para comparar senhas no processo de autenticação.

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("usuarioCriado");
    }

    public TokenResponseDto login(LoginRequestDto dto) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(//
                new UsernamePasswordAuthenticationToken(
                    dto.email(), dto.password()
                ));

                String token = tokenProvider.generateToken(authentication);

                return new TokenResponseDto(token, expiration);
           
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Credenciais Inválidas");
        } catch (Exception e) {
            throw new Exception("Erro ao autenticar usuário", e);
        }
    }
}
