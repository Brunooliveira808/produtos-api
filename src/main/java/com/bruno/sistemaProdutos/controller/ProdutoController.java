package com.bruno.sistemaProdutos.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
		@RequestParam(required = false) BigDecimal precoMin,
		@RequestParam(required = false) BigDecimal precoMax) {

		return ResponseEntity.ok(produtoService.busca(categoria, precoMin, precoMax));
	}

	@GetMapping("/pagina")
	public ResponseEntity<Page<ProdutoResponse>> getProdutosPorPagina(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(produtoService.listarProdutosPorPagina(pageable));
	}

	@GetMapping("/pagina/{page}")
	public ResponseEntity<Page<ProdutoResponse>> getProdutosPorPaginaPath(
			@PathVariable int page,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Pageable customPageable = org.springframework.data.domain.PageRequest.of(Math.max(0, page - 1), pageable.getPageSize(), pageable.getSort());
		return ResponseEntity.ok(produtoService.listarProdutosPorPagina(customPageable));
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
	public ResponseEntity<Page<ProdutoResponse>> getProdutosByFaixaDePrecoQuery(
			@RequestParam BigDecimal precoMin,
			@RequestParam BigDecimal precoMax,
			@PageableDefault(page = 0, size = 10, sort = "preco", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(produtoService.listarProdutosPorFaixaDePreco(precoMin, precoMax, pageable));
	}

	@GetMapping("/faixa-preco/{precoMin}/{precoMax}")
	public ResponseEntity<Page<ProdutoResponse>> getProdutosByFaixaDePreco(
			@PathVariable BigDecimal precoMin,
			@PathVariable BigDecimal precoMax,
			@PageableDefault(page = 0, size = 10, sort = "preco", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(produtoService.listarProdutosPorFaixaDePreco(precoMin, precoMax, pageable));
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
