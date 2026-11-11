package com.bruno.sistemaProdutos.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(

    @NotBlank
    String email,

    @NotBlank
    String password
) {

}
