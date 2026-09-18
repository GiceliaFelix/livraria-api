package com.gi.livraria.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    @NotBlank(message = "O titulo e obrigatorio")
    private String title;

    @NotBlank(message = "O autor e obrigatorio")
    private String author;

    @Pattern(regexp = "^[0-9Xx-]{10,20}$", message = "ISBN em formato invalido")
    private String isbn;

    private String publisher;

    private String publishedDate;

    @Positive(message = "O numero de paginas deve ser positivo")
    private Integer pageCount;

    private String category;

    @NotNull(message = "O preco e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preco nao pode ser negativo")
    private BigDecimal price;

    @NotNull(message = "A quantidade em estoque e obrigatoria")
    @Min(value = 0, message = "A quantidade em estoque nao pode ser negativa")
    private Integer quantityInStock;

    private String description;

    private String coverImageUrl;
}
