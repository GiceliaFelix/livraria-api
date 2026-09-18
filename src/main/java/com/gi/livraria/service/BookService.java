package com.gi.livraria.service;

import com.gi.livraria.dto.BookMapper;
import com.gi.livraria.dto.BookRequestDTO;
import com.gi.livraria.dto.BookResponseDTO;
import com.gi.livraria.exception.BookNotFoundException;
import com.gi.livraria.exception.DuplicateIsbnException;
import com.gi.livraria.model.Book;
import com.gi.livraria.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final GoogleBooksService googleBooksService;

    public BookService(BookRepository bookRepository, GoogleBooksService googleBooksService) {
        this.bookRepository = bookRepository;
        this.googleBooksService = googleBooksService;
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks(String title, String category) {
        List<Book> books;
        if (title != null && !title.isBlank()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (category != null && !category.isBlank()) {
            books = bookRepository.findByCategoryIgnoreCase(category);
        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(BookMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return BookMapper.toResponseDto(book);
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        validateIsbnNotDuplicated(dto.getIsbn());
        Book saved = bookRepository.save(BookMapper.toEntity(dto));
        log.info("Livro criado manualmente: id={}, titulo={}", saved.getId(), saved.getTitle());
        return BookMapper.toResponseDto(saved);
    }

    /**
     * Busca os dados do livro na Google Books API a partir do ISBN
     * e ja salva o resultado no catalogo local.
     */
    @Transactional
    public BookResponseDTO importBookByIsbn(String isbn) {
        validateIsbnNotDuplicated(isbn);
        BookRequestDTO fetched = googleBooksService.searchByIsbn(isbn);

        Book book = BookMapper.toEntity(fetched);
        book.setImportedFromExternalApi(true);
        Book saved = bookRepository.save(book);

        log.info("Livro importado da Google Books API: id={}, titulo={}, isbn={}",
                saved.getId(), saved.getTitle(), isbn);
        return BookMapper.toResponseDto(saved);
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book book = findBookOrThrow(id);

        boolean isbnChanged = dto.getIsbn() != null && !dto.getIsbn().equals(book.getIsbn());
        if (isbnChanged) {
            validateIsbnNotDuplicated(dto.getIsbn());
        }

        BookMapper.updateEntityFromDto(book, dto);
        Book updated = bookRepository.save(book);
        return BookMapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookOrThrow(id);
        bookRepository.delete(book);
        log.info("Livro removido: id={}", id);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> BookNotFoundException.byId(id));
    }

    private void validateIsbnNotDuplicated(String isbn) {
        if (isbn != null && bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateIsbnException(isbn);
        }
    }
}
