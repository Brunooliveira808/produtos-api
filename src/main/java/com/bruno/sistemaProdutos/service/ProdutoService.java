package com.bruno.sistemaProdutos.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.bruno.sistemaProdutos.dto.produto.ProdutoRequest;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResponse;
import com.bruno.sistemaProdutos.entity.Produto;
import com.bruno.sistemaProdutos.exception.NotFoundException;
import com.bruno.sistemaProdutos.mapper.ProdutoMapper;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;
import com.bruno.sistemaProdutos.repository.ProdutoRepository;

@Service
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


	public ProdutoResponse salvar(ProdutoRequest request) {
		Produto produto = produtoMapper.toEntity(request);
		produtoRepository.save(produto);
		return produtoMapper.toResponse(produto);
	}

	public ProdutoResponse buscarPorId(Long id) {
		Produto produto = produtoRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado"));
		
		return produtoMapper.toResponse(produto);
	}
	
	public List<ProdutoResponse> listarProdutosPorCategoria(String categoria) {
		return produtoRepository.findByCategoriasNomeIgnoreCase(categoria)
				.stream()
				.map(produtoMapper::toResponse)
				.toList();
	}

	public List<ProdutoResponse> listarTodosProdutos() {
		return produtoRepository.findAll().stream().map(produtoMapper::toResponse).toList();
	}

	public List<ProdutoResponse> listarProdutosPorNome(String nome) {
		return produtoRepository.findByNomeContainingIgnoreCase(nome)
				.stream()
				.map(produtoMapper::toResponse)
				.toList();
	}
	
	public List<ProdutoResponse> listarProdutosPorPagina(int page) {
		int pageSize = 9; // Defina o tamanho da página
		int offset = (page - 1) * pageSize; // Calcule o deslocamento com base na página

		return produtoRepository.findAll()
				.stream()
				.skip(offset) // Pule os registros anteriores
				.limit(pageSize) // Limite o número de registros retornados
				.map(produtoMapper::toResponse)
				.toList();
	}

	public List<ProdutoResponse> listarProdutosPorFaixaDePreco(double precoMin, double precoMax) {
		return produtoRepository.findAll()
				.stream()
				.filter(produto -> produto.getPreco() >= precoMin && produto.getPreco() <= precoMax)
				.map(produtoMapper::toResponse)
				.toList();
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
 