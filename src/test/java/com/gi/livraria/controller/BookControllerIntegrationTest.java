package com.gi.livraria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gi.livraria.dto.BookRequestDTO;
import com.gi.livraria.dto.BookResponseDTO;
import com.gi.livraria.exception.BookNotFoundException;
import com.gi.livraria.exception.DuplicateIsbnException;
import com.gi.livraria.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao da camada web: sobem apenas o contexto do
 * BookController (via @WebMvcTest), com o BookService mockado.
 * Cobrem serializacao/deserializacao JSON, status HTTP e o
 * mapeamento das excecoes de negocio feito pelo GlobalExceptionHandler.
 */
@WebMvcTest(BookController.class)
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookResponseDTO sampleResponse() {
        return BookResponseDTO.builder()
                .id(1L)
                .title("Codigo Limpo")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .price(new BigDecimal("89.90"))
                .quantityInStock(5)
                .importedFromExternalApi(false)
                .build();
    }

    @Test
    void deveListarTodosOsLivros() throws Exception {
        when(bookService.getAllBooks(null, null)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Codigo Limpo"))
                .andExpect(jsonPath("$[0].isbn").value("9788576082675"));
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }

    @Test
    void deveRetornar404QuandoLivroNaoExiste() throws Exception {
        when(bookService.getBookById(99L)).thenThrow(BookNotFoundException.byId(99L));

        mockMvc.perform(get("/api/books/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarLivroComDadosValidos() throws Exception {
        BookRequestDTO request = BookRequestDTO.builder()
                .title("Codigo Limpo")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .price(new BigDecimal("89.90"))
                .quantityInStock(5)
                .build();

        when(bookService.createBook(any(BookRequestDTO.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Codigo Limpo"));
    }

    @Test
    void deveRetornar400QuandoDadosObrigatoriosFaltam() throws Exception {
        // titulo, autor, preco e quantidade sao obrigatorios (Bean Validation)
        BookRequestDTO invalido = BookRequestDTO.builder().build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(bookService, never()).createBook(any());
    }

    @Test
    void deveRetornar409QuandoIsbnDuplicado() throws Exception {
        BookRequestDTO request = BookRequestDTO.builder()
                .title("Codigo Limpo")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .price(new BigDecimal("89.90"))
                .quantityInStock(5)
                .build();

        when(bookService.createBook(any(BookRequestDTO.class)))
                .thenThrow(new DuplicateIsbnException("9788576082675"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveImportarLivroPorIsbn() throws Exception {
        when(bookService.importBookByIsbn(anyString())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/books/import/{isbn}", "9788576082675"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.importedFromExternalApi").value(false));
    }

    @Test
    void deveAtualizarLivroExistente() throws Exception {
        BookRequestDTO request = BookRequestDTO.builder()
                .title("Codigo Limpo (2a edicao)")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .price(new BigDecimal("99.90"))
                .quantityInStock(3)
                .build();

        BookResponseDTO updated = sampleResponse();
        updated.setTitle("Codigo Limpo (2a edicao)");

        when(bookService.updateBook(eq(1L), any(BookRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Codigo Limpo (2a edicao)"));
    }

    @Test
    void deveDeletarLivroERetornar204() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }
}
