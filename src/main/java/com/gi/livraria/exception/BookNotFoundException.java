package com.gi.livraria.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public static BookNotFoundException byId(Long id) {
        return new BookNotFoundException("Livro nao encontrado com id: " + id);
    }

    public static BookNotFoundException byIsbn(String isbn) {
        return new BookNotFoundException("Nenhum livro encontrado na Google Books API para o ISBN: " + isbn);
    }
}
