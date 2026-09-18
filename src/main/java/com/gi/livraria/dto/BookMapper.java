package com.gi.livraria.dto;

import com.gi.livraria.model.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(BookRequestDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .publisher(dto.getPublisher())
                .publishedDate(dto.getPublishedDate())
                .pageCount(dto.getPageCount())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .quantityInStock(dto.getQuantityInStock())
                .description(dto.getDescription())
                .coverImageUrl(dto.getCoverImageUrl())
                .build();
    }

    public static void updateEntityFromDto(Book book, BookRequestDTO dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setPublishedDate(dto.getPublishedDate());
        book.setPageCount(dto.getPageCount());
        book.setCategory(dto.getCategory());
        book.setPrice(dto.getPrice());
        book.setQuantityInStock(dto.getQuantityInStock());
        book.setDescription(dto.getDescription());
        book.setCoverImageUrl(dto.getCoverImageUrl());
    }

    public static BookResponseDTO toResponseDto(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .pageCount(book.getPageCount())
                .category(book.getCategory())
                .price(book.getPrice())
                .quantityInStock(book.getQuantityInStock())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .importedFromExternalApi(book.getImportedFromExternalApi())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
