package com.bruno.sistemaProdutos.service;

import java.util.List;

import com.bruno.sistemaProdutos.mapper.ProdutoMapper;
import org.springframework.stereotype.Service;

import com.bruno.sistemaProdutos.dto.categoria.CategoriaRequest;
import com.bruno.sistemaProdutos.dto.categoria.CategoriaResponse;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResumoResponse;
import com.bruno.sistemaProdutos.entity.Categoria;
import com.bruno.sistemaProdutos.exception.NotFoundException;
import com.bruno.sistemaProdutos.mapper.CategoriaMapper;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;

@Service
public class CategoriaService {

	
	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;
	private final ProdutoMapper produtoMapper;
	
	public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper, ProdutoMapper produtoMapper) {
		super();
		this.categoriaRepository = categoriaRepository;
		this.categoriaMapper = categoriaMapper;
		this.produtoMapper = produtoMapper;
	}
	
	public CategoriaResponse salvar(CategoriaRequest request) {
		Categoria categoria = categoriaMapper.toEntity(request);
		categoriaRepository.save(categoria);
		return categoriaMapper.toResponse(categoria);
	}

	public List<CategoriaResponse> listarTodas(){
		return categoriaRepository.findAll().stream().map(categoriaMapper::toResponse).toList();
	}
	
	
	public List<ProdutoResumoResponse> listarProdutosPorCategoria(Long Id) {
		
		Categoria categoria = categoriaRepository.findById(Id).orElseThrow(() -> new NotFoundException("Essa categoria não existe"));
		
	    return categoria.getProdutos()
	            .stream()
	            .map(produtoMapper::toResumoResponse).toList();
	}
	
	
	
	
}
