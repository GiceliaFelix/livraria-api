package com.gi.livraria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publishedDate;
    private Integer pageCount;
    private String category;
    private BigDecimal price;
    private Integer quantityInStock;
    private String description;
    private String coverImageUrl;
    private Boolean importedFromExternalApi;
    private LocalDateTime createdAt;
}
