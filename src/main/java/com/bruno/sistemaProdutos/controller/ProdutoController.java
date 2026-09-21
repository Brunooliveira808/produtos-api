package com.bruno.sistemaProdutos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping
	public ResponseEntity<List<ProdutoResponse>> busca(
		@RequestParam(required = false) String categoria,
		@RequestParam(required = false) double precoMin,
		@RequestParam(required = false) double precoMax) {

		return ResponseEntity.ok(produtoService.busca(categoria, precoMin, precoMax));
	}

	@GetMapping("/pagina/{page}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorPagina(@PathVariable int page) {
		return ResponseEntity.ok(produtoService.listarProdutosPorPagina(page));
	}

	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosPorNome(@PathVariable String nome) {
		return ResponseEntity.ok(produtoService.listarProdutosPorNome(nome));
	}



	@GetMapping("{id}")
	public ResponseEntity<ProdutoResponse> produtoById(@PathVariable Long id) {
		return ResponseEntity.ok(produtoService.buscarPorId(id));
	}

	@GetMapping("/categoria")
	public ResponseEntity<List<ProdutoResponse>> getProdutosByCategoriaQuery(@RequestParam String categoria) {
		return ResponseEntity.ok(produtoService.listarProdutosPorCategoria(categoria));
	}

	@GetMapping("/categoria/{categoria}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosByCategoria(@PathVariable String categoria) {
		return ResponseEntity.ok(produtoService.listarProdutosPorCategoria(categoria));
	}

	@GetMapping("/faixa-preco")
	public ResponseEntity<List<ProdutoResponse>> getProdutosByFaixaDePrecoQuery(@RequestParam double precoMin,
																				@RequestParam double precoMax) {
		return ResponseEntity.ok(produtoService.listarProdutosPorFaixaDePreco(precoMin, precoMax));
	}

	@GetMapping("/faixa-preco/{precoMin}/{precoMax}")
	public ResponseEntity<List<ProdutoResponse>> getProdutosByFaixaDePreco(@PathVariable double precoMin,
			@PathVariable double precoMax) {
		return ResponseEntity.ok(produtoService.listarProdutosPorFaixaDePreco(precoMin, precoMax));
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
