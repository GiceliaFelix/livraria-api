package com.gi.livraria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Representa o corpo de resposta da Google Books API para uma busca.
 * Endpoint: https://www.googleapis.com/books/v1/volumes?q=isbn:{isbn}
 * Apenas os campos usados pelo projeto sao mapeados; o restante e ignorado.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponseDTO {

    private Integer totalItems;
    private List<Item> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private VolumeInfo volumeInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private Integer pageCount;
        private List<String> categories;
        private ImageLinks imageLinks;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        private String thumbnail;
    }
}
