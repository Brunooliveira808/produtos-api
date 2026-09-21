package com.bruno.sistemaProdutos.dto.produto;

import java.util.List;

public record ProdutoResponse(

        Long id,
        String nome,
        double preco,
        List<String> categorias) {

}
