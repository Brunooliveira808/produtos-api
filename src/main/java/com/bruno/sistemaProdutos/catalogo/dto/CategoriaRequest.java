package com.bruno.sistemaProdutos.catalogo.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
		
		@NotBlank
		String nome) {

}
