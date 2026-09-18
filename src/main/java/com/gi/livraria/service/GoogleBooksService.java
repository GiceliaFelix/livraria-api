package com.gi.livraria.service;

import com.gi.livraria.dto.BookRequestDTO;
import com.gi.livraria.dto.GoogleBooksResponseDTO;
import com.gi.livraria.exception.BookNotFoundException;
import com.gi.livraria.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Responsavel por consumir a Google Books API externa e converter
 * a resposta em um BookRequestDTO pronto para ser salvo no catalogo.
 *
 * Documentacao da API: https://developers.google.com/books/docs/v1/using
 * Nao requer chave de API para buscas simples por ISBN.
 */
@Service
@Slf4j
public class GoogleBooksService {

    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:{isbn}";

    private final RestTemplate restTemplate;

    @Value("${livraria.import.default-price:39.90}")
    private BigDecimal defaultPrice;

    public GoogleBooksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookRequestDTO searchByIsbn(String isbn) {
        GoogleBooksResponseDTO response;
        try {
            response = restTemplate.getForObject(GOOGLE_BOOKS_URL, GoogleBooksResponseDTO.class, isbn);
        } catch (RestClientException ex) {
            log.error("Falha ao chamar a Google Books API para o ISBN {}: {}", isbn, ex.getMessage());
            throw new ExternalApiException("Nao foi possivel consultar a Google Books API no momento", ex);
        }

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            throw BookNotFoundException.byIsbn(isbn);
        }

        GoogleBooksResponseDTO.VolumeInfo info = response.getItems().get(0).getVolumeInfo();

        return BookRequestDTO.builder()
                .title(info.getTitle())
                .author(joinAuthors(info.getAuthors()))
                .isbn(isbn)
                .publisher(info.getPublisher())
                .publishedDate(info.getPublishedDate())
                .pageCount(info.getPageCount())
                .category(firstCategoryOrDefault(info.getCategories()))
                .description(info.getDescription())
                .coverImageUrl(info.getImageLinks() != null ? info.getImageLinks().getThumbnail() : null)
                .price(defaultPrice)
                .quantityInStock(1)
                .build();
    }

    private String joinAuthors(List<String> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Autor desconhecido";
        }
        return String.join(", ", authors);
    }

    private String firstCategoryOrDefault(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return "Geral";
        }
        return categories.get(0);
    }
}
