package com.bruno.sistemaProdutos.dto;

public record TokenResponseDto(
        String token,
        long expiresIn) {
}
