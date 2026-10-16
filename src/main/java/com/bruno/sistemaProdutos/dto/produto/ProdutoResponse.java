package com.bruno.sistemaProdutos.dto.produto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponse(

        Long id,
        String nome,
        BigDecimal preco,
        List<String> categorias) {

}
