package com.bruno.sistemaProdutos.catalogo.mapper;

import org.springframework.stereotype.Component;

import com.bruno.sistemaProdutos.catalogo.dto.CategoriaRequest;
import com.bruno.sistemaProdutos.catalogo.dto.CategoriaResponse;
import com.bruno.sistemaProdutos.catalogo.entity.Categoria;

@Component
public class CategoriaMapper {

	public Categoria toEntity(CategoriaRequest request) {
		 
		Categoria categoria = new Categoria();
		
		categoria.setNome(request.nome());
		
		return categoria;
	}
	
	public CategoriaResponse toResponse(Categoria categoria) {
		return new CategoriaResponse(categoria.getId(), categoria.getNome());
	}
}
