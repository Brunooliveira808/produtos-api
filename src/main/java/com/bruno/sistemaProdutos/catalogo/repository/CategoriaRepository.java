package com.bruno.sistemaProdutos.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bruno.sistemaProdutos.catalogo.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
