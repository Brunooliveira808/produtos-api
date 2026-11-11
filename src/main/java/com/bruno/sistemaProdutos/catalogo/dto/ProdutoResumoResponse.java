package com.bruno.sistemaProdutos.catalogo.dto;

import java.math.BigDecimal;

public record ProdutoResumoResponse(
        Long id,
        String nome,
        BigDecimal preco) {
}
