package com.bruno.sistemaProdutos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bruno.sistemaProdutos.dto.LoginRequestDto;
import com.bruno.sistemaProdutos.dto.RegisterRequestDto;
import com.bruno.sistemaProdutos.dto.TokenResponseDto;
import com.bruno.sistemaProdutos.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        if (registerRequestDto.email().isBlank() || registerRequestDto.password().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("O nome do usuário e senha são obrigatórios.");
        }
        return authenticationService.register(registerRequestDto);
    }
    
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto) throws Exception {
        return authenticationService.login(loginRequestDto);
    }
}
