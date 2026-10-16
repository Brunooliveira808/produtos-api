package com.bruno.sistemaProdutos.dto.produto;

import java.math.BigDecimal;

public record ProdutoResumoResponse(
        Long id,
        String nome,
        BigDecimal preco) {
}
