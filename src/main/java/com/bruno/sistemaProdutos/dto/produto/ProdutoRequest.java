package com.bruno.sistemaProdutos.dto.produto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record ProdutoRequest(
		
		@NotBlank
		String nome, 
		
		@NotNull
		@Positive
		BigDecimal preco, 
		
		@NotEmpty
		List<Long> categoriasIds) {

}
