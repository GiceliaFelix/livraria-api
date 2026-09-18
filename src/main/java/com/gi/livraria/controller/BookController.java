package com.gi.livraria.controller;

import com.gi.livraria.dto.BookRequestDTO;
import com.gi.livraria.dto.BookResponseDTO;
import com.gi.livraria.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Operacoes de catalogo de livros")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os livros, com filtro opcional por titulo ou categoria")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(bookService.getAllBooks(title, category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro pelo id")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um livro manualmente")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO dto) {
        BookResponseDTO created = bookService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/import/{isbn}")
    @Operation(summary = "Busca o livro na Google Books API pelo ISBN e ja cadastra no catalogo")
    public ResponseEntity<BookResponseDTO> importBookByIsbn(@PathVariable String isbn) {
        BookResponseDTO imported = bookService.importBookByIsbn(isbn);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de um livro existente")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um livro do catalogo")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
