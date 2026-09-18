package com.gi.livraria.service;

import com.gi.livraria.dto.BookRequestDTO;
import com.gi.livraria.dto.BookResponseDTO;
import com.gi.livraria.exception.BookNotFoundException;
import com.gi.livraria.exception.DuplicateIsbnException;
import com.gi.livraria.model.Book;
import com.gi.livraria.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GoogleBooksService googleBooksService;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .id(1L)
                .title("Codigo Limpo")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .price(new BigDecimal("89.90"))
                .quantityInStock(5)
                .build();
    }

    @Test
    void deveRetornarLivroQuandoIdExiste() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        BookResponseDTO result = bookService.getBookById(1L);

        assertThat(result.getTitle()).isEqualTo("Codigo Limpo");
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExiste() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoAoCriarLivroComIsbnDuplicado() {
        BookRequestDTO dto = BookRequestDTO.builder()
                .title("Outro Livro")
                .author("Autor X")
                .isbn("9788576082675")
                .price(BigDecimal.TEN)
                .quantityInStock(1)
                .build();

        when(bookRepository.existsByIsbn("9788576082675")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(dto))
                .isInstanceOf(DuplicateIsbnException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void deveImportarLivroDaGoogleBooksApi() {
        String isbn = "9780134685991";
        BookRequestDTO fetched = BookRequestDTO.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn(isbn)
                .price(new BigDecimal("39.90"))
                .quantityInStock(1)
                .build();

        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(googleBooksService.searchByIsbn(isbn)).thenReturn(fetched);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });

        BookResponseDTO result = bookService.importBookByIsbn(isbn);

        assertThat(result.getTitle()).isEqualTo("Effective Java");
        assertThat(result.getImportedFromExternalApi()).isTrue();
        verify(googleBooksService).searchByIsbn(isbn);
    }

    @Test
    void deveDeletarLivroExistente() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(sampleBook);
    }
}
