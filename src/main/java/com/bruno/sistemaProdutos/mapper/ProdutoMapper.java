package com.bruno.sistemaProdutos.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bruno.sistemaProdutos.dto.produto.ProdutoRequest;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResponse;
import com.bruno.sistemaProdutos.entity.Categoria;
import com.bruno.sistemaProdutos.entity.Produto;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;

@Component
public class ProdutoMapper {
	
	private final CategoriaRepository categoriaRepository;
	
    public ProdutoMapper(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

	public Produto toEntity(ProdutoRequest request) {
		
		List<Categoria> categorias = categoriaRepository.findAllById(request.categoriasIds());					
		 
		if (categorias.size() != request.categoriasIds().size()) {
			throw new RuntimeException("Uma ou mais categorias não existem");
		}
				
		Produto produto = new Produto();
		produto.setNome(request.nome());
		produto.setPreco(request.preco());
		produto.setCategorias(categorias);

		return produto;
	}
	
	public ProdutoResponse toResponse(Produto produto) {
		
		return new ProdutoResponse(
			produto.getId(),
			produto.getNome(),
			produto.getPreco(),
			produto.getCategorias().stream()
				.map(Categoria::getNome)
				.toList()
		);
	}
}
