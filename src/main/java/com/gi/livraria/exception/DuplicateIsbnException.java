package com.gi.livraria.exception;

public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("Ja existe um livro cadastrado com o ISBN: " + isbn);
    }
}
