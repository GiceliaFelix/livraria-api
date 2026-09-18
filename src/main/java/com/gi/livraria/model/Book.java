package com.gi.livraria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um livro no catalogo da livraria.
 */
@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, length = 20)
    private String isbn;

    private String publisher;

    private String publishedDate;

    private Integer pageCount;

    private String category;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Builder.Default
    private Integer quantityInStock = 0;

    @Column(length = 2000)
    private String description;

    private String coverImageUrl;

    /** Marca se o registro foi criado via importacao da Google Books API. */
    @Builder.Default
    private Boolean importedFromExternalApi = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
