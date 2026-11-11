package com.bruno.sistemaProdutos.auth.dto;

public record TokenResponseDto(
        String token,
        long expiresIn) {
}
