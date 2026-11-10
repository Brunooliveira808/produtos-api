package com.bruno.sistemaProdutos.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bruno.sistemaProdutos.dto.produto.ProdutoRequest;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResponse;
import com.bruno.sistemaProdutos.entity.Produto;
import com.bruno.sistemaProdutos.exception.NotFoundException;
import com.bruno.sistemaProdutos.mapper.ProdutoMapper;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;
import com.bruno.sistemaProdutos.repository.ProdutoRepository;

@Service
@Transactional
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final CategoriaRepository categoriaRepository;
	private final ProdutoMapper produtoMapper;
	
	
	public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
			ProdutoMapper produtoMapper) {
		super();
		this.produtoRepository = produtoRepository;
		this.categoriaRepository = categoriaRepository;
		this.produtoMapper = produtoMapper;
	}

	@Transactional(readOnly = true)
	public List<ProdutoResponse> busca(String categoria, BigDecimal precoMin, BigDecimal precoMax) {
		return produtoRepository.buscarPorFiltros(categoria, precoMin, precoMax)
				.stream()
				.map(produtoMapper::toResponse)
				.toList();
	}

	public ProdutoResponse salvar(ProdutoRequest request) {
		Produto produto = produtoMapper.toEntity(request);
		produtoRepository.save(produto);
		return produtoMapper.toResponse(produto);
	}

	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(Long id) {
		Produto produto = produtoRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));

		return produtoMapper.toResponse(produto);
	}
	
	@Transactional(readOnly = true)
	public List<ProdutoResponse> listarProdutosPorCategoria(String categoria) {
		return produtoRepository.findByCategoriasNomeIgnoreCase(categoria)
				.stream()
				.map(produtoMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ProdutoResponse> listarProdutosPorNome(String nome) {
		return produtoRepository.findByNomeContainingIgnoreCase(nome)
				.stream()
				.map(produtoMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listarProdutosPorPagina(Pageable pageable) {
		return produtoRepository.findAll(pageable)
				.map(produtoMapper::toResponse);
	}

	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listarProdutosPorFaixaDePreco(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
		return produtoRepository.findByPrecoBetween(precoMin, precoMax, pageable)
				.map(produtoMapper::toResponse);
	}
	
	public void removerProduto(Long id) {
		if(!produtoRepository.existsById(id)) {
			throw new NotFoundException("ID do produto não encontrado");
		} else {
			produtoRepository.deleteById(id);
		}
	}
	
	public ProdutoResponse atualizarProduto(Long id, ProdutoRequest request) {

		Produto produto = produtoRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
		produto.setNome(request.nome());
		produto.setPreco(request.preco());
		produto.setCategorias(categoriaRepository.findAllById(request.categoriasIds()));

	    produto = produtoRepository.save(produto);

	    return produtoMapper.toResponse(produto);
	}
	
}
 