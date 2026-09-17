package com.bruno.sistemaProdutos.mapper;

import org.springframework.stereotype.Component;

import com.bruno.sistemaProdutos.dto.categoria.CategoriaRequest;
import com.bruno.sistemaProdutos.dto.categoria.CategoriaResponse;
import com.bruno.sistemaProdutos.entity.Categoria;

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
