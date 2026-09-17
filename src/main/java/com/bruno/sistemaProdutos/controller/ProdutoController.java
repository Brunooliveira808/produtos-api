package com.bruno.sistemaProdutos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bruno.sistemaProdutos.dto.produto.ProdutoRequest;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResponse;
import com.bruno.sistemaProdutos.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping("/pagina/{page}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorPagina(@PathVariable int page) {
		return ResponseEntity.ok(produtoService.listarProdutosPorPagina(page));
	}

	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorNome(@PathVariable String nome) {
		return ResponseEntity.ok(produtoService.listarProdutosPorNome(nome));
	}

	@GetMapping("/categoria/{categoria}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorCategoria(@PathVariable String categoria) {
		return ResponseEntity.ok(produtoService.listarProdutosPorCategoria(categoria));
	}

	@GetMapping("/faixa-preco/{precoMin}/{precoMax}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorFaixaDePreco(@PathVariable double precoMin,
			@PathVariable double precoMax) {
		return ResponseEntity.ok(produtoService.listarProdutosPorFaixaDePreco(precoMin, precoMax));
	}

	@GetMapping("{id}")
	public ResponseEntity<ProdutoResponse> produtoById(@PathVariable Long id) {
		return ResponseEntity.ok(produtoService.buscarPorId(id));
	}

	@PostMapping
	public ProdutoResponse createProduto(@Valid @RequestBody ProdutoRequest request) {
		return produtoService.salvar(request);
	}

	@PutMapping("{id}")
	public ProdutoResponse atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequest request) {

		return produtoService.atualizarProduto(id, request);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {

		produtoService.removerProduto(id);
		return ResponseEntity.noContent().build();
	}
}
