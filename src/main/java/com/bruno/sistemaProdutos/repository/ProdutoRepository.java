package com.bruno.sistemaProdutos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bruno.sistemaProdutos.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

    List<Produto> findByNomeContainingIgnoreCase(String nome);
	
    List<Produto> findByCategoriasNomeIgnoreCase(String categoria);

    Page<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable);

}
