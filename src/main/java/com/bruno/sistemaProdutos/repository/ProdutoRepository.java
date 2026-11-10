package com.bruno.sistemaProdutos.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bruno.sistemaProdutos.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

    List<Produto> findByNomeContainingIgnoreCase(String nome);
	
    List<Produto> findByCategoriasNomeIgnoreCase(String categoria);

    Page<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable);

    @Query("""
        SELECT p FROM Produto p 
        JOIN p.categorias c 
        WHERE (:categoria IS NULL OR LOWER(c.nome) = LOWER(:categoria))
          AND (:precoMin IS NULL OR p.preco >= :precoMin)
          AND (:precoMax IS NULL OR p.preco <= :precoMax)
    """)
    List<Produto> buscarPorFiltros(
        @Param("categoria") String categoria,
        @Param("precoMin") BigDecimal precoMin,
        @Param("precoMax") BigDecimal precoMax
    );

}
