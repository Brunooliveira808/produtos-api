package com.bruno.sistemaProdutos.catalogo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bruno.sistemaProdutos.catalogo.dto.CategoriaRequest;
import com.bruno.sistemaProdutos.catalogo.dto.CategoriaResponse;
import com.bruno.sistemaProdutos.catalogo.dto.ProdutoResumoResponse;
import com.bruno.sistemaProdutos.catalogo.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		super();
		this.categoriaService = categoriaService;
	}
	
	
	@GetMapping
	public ResponseEntity<List<CategoriaResponse>> getCategorias(){
		
		return ResponseEntity.ok(categoriaService.listarTodas());
	}
	
	
	@GetMapping("{id}")
	public List<ProdutoResumoResponse> getCategoriaPorProduto(@PathVariable Long id) {
		return categoriaService.listarProdutosPorCategoria(id);
	}
	
	@PostMapping
	public CategoriaResponse createCategoria(@Valid @RequestBody CategoriaRequest request) {
		return categoriaService.salvar(request);
	}
}
