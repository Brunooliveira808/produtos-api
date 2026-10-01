package com.bruno.sistemaProdutos.service;

import com.bruno.sistemaProdutos.dto.produto.ProdutoRequest;
import com.bruno.sistemaProdutos.dto.produto.ProdutoResponse;
import com.bruno.sistemaProdutos.entity.Categoria;
import com.bruno.sistemaProdutos.entity.Produto;
import com.bruno.sistemaProdutos.exception.NotFoundException;
import com.bruno.sistemaProdutos.mapper.ProdutoMapper;
import com.bruno.sistemaProdutos.repository.CategoriaRepository;
import com.bruno.sistemaProdutos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequest produtoRequest;
    private ProdutoResponse produtoResponse;
    private Categoria categoria;

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

        produtoRequest = new ProdutoRequest("Smartphone", 1500.0, List.of(1L));
        produtoResponse = new ProdutoResponse(1L, "Smartphone", 1500.0, List.of("Eletrônicos"));
    }

    @Test
    @DisplayName("Deve salvar um produto com sucesso")
    public void deveSalvarProdutoComSucesso() {
        when(produtoMapper.toEntity(produtoRequest)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        ProdutoResponse resultado = produtoService.salvar(produtoRequest);

        assertNotNull(resultado);
        assertEquals(resultado.id(), produtoResponse.id());
        assertEquals(resultado.nome(), produtoResponse.nome());
        assertEquals(resultado.preco(), produtoResponse.preco());
        assertEquals(resultado.categorias(), produtoResponse.categorias());

        verify(produtoMapper, times(1)).toEntity(produtoRequest);
        verify(produtoRepository, times(1)).save(produto);
        verify(produtoMapper, times(1)).toResponse(produto);
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    public void deveBuscarPorId(){
        when(produtoRepository.findById(1L)).thenReturn(Optional.ofNullable(produto));
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        ProdutoResponse resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(resultado.id(), produtoResponse.id());
        assertEquals(resultado.nome(), produtoResponse.nome());
        assertEquals(resultado.preco(), produtoResponse.preco());
        assertEquals(resultado.categorias(), produtoResponse.categorias());

        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoMapper, times(1)).toResponse(produto);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando produto não for encontrado por ID")
    void deveLancarNotFoundExceptionQuandoProdutoNaoEncontradoPorId() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            produtoService.buscarPorId(99L);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(produtoRepository, times(1)).findById(99L);
        verify(produtoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria e faixa de preco")
    void deveBuscarProdutosPorCategoriaEFaixaDePreco() {
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Notebook");
        produto2.setPreco(3000.0);
        produto2.setCategorias(List.of(categoria));

        when(produtoRepository.findByCategoriasNomeIgnoreCase("Eletrônicos")).thenReturn(List.of(produto, produto2));
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        List<ProdutoResponse> resultado = produtoService.busca("Eletrônicos", 1000.0, 2000.0);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.get(0).nome());
        verify(produtoRepository, times(1)).findByCategoriasNomeIgnoreCase("Eletrônicos");
    }

    @Test
    @DisplayName("Deve listar produtos por categoria")
    void deveListarProdutosPorCategoria() {
        when(produtoRepository.findByCategoriasNomeIgnoreCase("Eletrônicos")).thenReturn(List.of(produto));
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        List<ProdutoResponse> resultado = produtoService.listarProdutosPorCategoria("Eletrônicos");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.getFirst().nome());
        verify(produtoRepository, times(1)).findByCategoriasNomeIgnoreCase("Eletrônicos");
    }

    @Test
    @DisplayName("Deve listar produtos por nome")
    void deveListarProdutosPorNome(){
        when(produtoRepository.findByNomeContainingIgnoreCase("Smart")).thenReturn(List.of(produto));
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        List<ProdutoResponse> resultado = produtoService.listarProdutosPorNome("Smart");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.getFirst().nome());
        verify(produtoRepository, times(1)).findByNomeContainingIgnoreCase("Smart");
    }

    @Test
    @DisplayName("Deve listar produtos por página")
    void deveListarProdutosPorPagina() {
        List<Produto> listaProdutos = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Produto p = new Produto();
            p.setId((long) i);
            p.setNome("Produto " + i);
            p.setPreco(10.0 * i);
            p.setCategorias(List.of(categoria));
            listaProdutos.add(p);
        }

        when(produtoRepository.findAll()).thenReturn(listaProdutos);
        when(produtoMapper.toResponse(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            return new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(), List.of("Eletrônicos"));
        });

        // Página 1: 9 itens (offset 0 a 8)
        List<ProdutoResponse> pagina1 = produtoService.listarProdutosPorPagina(1);
        assertEquals(9, pagina1.size());
        assertEquals("Produto 1", pagina1.get(0).nome());
        assertEquals("Produto 9", pagina1.get(8).nome());

        // Página 2: 6 itens (offset 9 a 14)
        List<ProdutoResponse> pagina2 = produtoService.listarProdutosPorPagina(2);
        assertEquals(6, pagina2.size());
        assertEquals("Produto 10", pagina2.get(0).nome());
        assertEquals("Produto 15", pagina2.get(5).nome());
    }

    @Test
    @DisplayName("Deve listar produtos por faixa de preço")
    void deveListarProdutoPorFaixaDePreco() {
        Produto barato = new Produto();
        barato.setId(2L);
        barato.setNome("Fone de Ouvido");
        barato.setPreco(50.0);

        Produto caro = new Produto();
        caro.setId(3L);
        caro.setNome("TV 4K");
        caro.setPreco(4000.0);

        when(produtoRepository.findAll()).thenReturn(List.of(produto, barato, caro));
        when(produtoMapper.toResponse(produto)).thenReturn(produtoResponse);

        List<ProdutoResponse> resultado = produtoService.listarProdutosPorFaixaDePreco(500.0, 2000.0);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.get(0).nome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve remover produto quando ID existir")
    void deveRemoverProdutoQuandoIdExistir() {
        when(produtoRepository.existsById(1L)).thenReturn(true);

        produtoService.removerProduto(1L);

        verify(produtoRepository, times(1)).existsById(1L);
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar remover produto com ID inexistente")
    void deveLancarNotFoundExceptionAoRemoverProdutoComIdInexistente() {
        when(produtoRepository.existsById(99L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            produtoService.removerProduto(99L);
        });

        assertEquals("ID do produto não encontrado", exception.getMessage());
        verify(produtoRepository, times(1)).existsById(99L);
        verify(produtoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        ProdutoRequest requestAtualizacao = new ProdutoRequest("Smartphone Pro", 2000.0, List.of(1L));
        ProdutoResponse responseAtualizado = new ProdutoResponse(1L, "Smartphone Pro", 2000.0, List.of("Eletrônicos"));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findAllById(List.of(1L))).thenReturn(List.of(categoria));
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(responseAtualizado);

        ProdutoResponse resultado = produtoService.atualizarProduto(1L, requestAtualizacao);

        assertNotNull(resultado);
        assertEquals("Smartphone Pro", resultado.nome());
        assertEquals(2000.0, resultado.preco());

        verify(produtoRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).findAllById(List.of(1L));
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar produto inexistente")
    void deveLancarNotFoundExceptionAoAtualizarProdutoInexistente() {
        ProdutoRequest requestAtualizacao = new ProdutoRequest("Smartphone Pro", 2000.0, List.of(1L));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            produtoService.atualizarProduto(99L, requestAtualizacao);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(produtoRepository, times(1)).findById(99L);
        verify(produtoRepository, never()).save(any());
    }
}
