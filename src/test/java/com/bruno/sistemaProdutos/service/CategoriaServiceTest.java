package com.bruno.sistemaProdutos.service;

import com.bruno.sistemaProdutos.dto.categoria.CategoriaRequest;
import com.bruno.sistemaProdutos.dto.categoria.CategoriaResponse;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResumoResponse;
import com.bruno.sistemaProdutos.entity.Categoria;
import com.bruno.sistemaProdutos.entity.Produto;
import com.bruno.sistemaProdutos.mapper.CategoriaMapper;
import com.bruno.sistemaProdutos.mapper.ProdutoMapper;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequest categoriaRequest;
    private CategoriaResponse categoriaResponse;
    private Produto produto;
    private ProdutoResumoResponse produtoResumoResponse;

    @BeforeEach
    void setup(){
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Smartphone");
        produto.setPreco(1500.0);
        produto.setCategorias(List.of(categoria));

        produtoResumoResponse = new ProdutoResumoResponse(1L, "Smartphone", 1500.0);
        categoriaRequest = new CategoriaRequest("Eletrônicos");
        categoriaResponse = new CategoriaResponse(1L, "Eletrônicos");
    }

    @Test
    @DisplayName("Deve criar categoria")
    public void deveCriarCategoria(){
        when(categoriaMapper.toEntity(categoriaRequest)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        CategoriaResponse resultado = categoriaService.salvar(categoriaRequest);

        assertNotNull(resultado);
        assertEquals(resultado.id(), categoriaResponse.id());
        assertEquals(resultado.nome(), categoriaResponse.nome());

        verify(categoriaMapper, times(1)).toEntity(categoriaRequest);
        verify(categoriaRepository, times(1)).save(categoria);
        verify(categoriaMapper, times(1)).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    public void deveListarTodasCategorias(){
        when(categoriaMapper.toEntity(categoriaRequest)).thenReturn(categoria);
        when(categoriaRepository.findAll()).thenReturn(Collections.singletonList(categoria));

        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponse(categoria)).thenReturn(categoriaResponse);

        categoriaService.salvar(categoriaRequest);
        List<CategoriaResponse> resultado = categoriaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(resultado.getFirst(), categoriaResponse);

        verify(categoriaMapper, times(2)).toResponse(categoria);
        verify(categoriaRepository,times(1)).findAll();
        verify(categoriaRepository,times(1)).save(categoria);
    }

    @Test
    @DisplayName("Deve Listar Produto Por Categoria")
    public void listarProdutosPorCategoria(){
        categoria.setProdutos(List.of(produto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoria));
        when(produtoMapper.toResumoResponse(produto)).thenReturn(produtoResumoResponse);

        List<ProdutoResumoResponse> resultado = categoriaService.listarProdutosPorCategoria(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getFirst().id());
        assertEquals("Smartphone", resultado.getFirst().nome());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(produtoMapper, times(1)).toResumoResponse(produto);

    }
}
