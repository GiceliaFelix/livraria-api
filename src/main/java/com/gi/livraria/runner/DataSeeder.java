package com.gi.livraria.runner;

import com.gi.livraria.model.Book;
import com.gi.livraria.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Popula o banco H2 com alguns livros de exemplo ao iniciar a aplicacao,
 * para facilitar testar a API sem precisar cadastrar nada na mao.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    public DataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        bookRepository.save(Book.builder()
                .title("Codigo Limpo")
                .author("Robert C. Martin")
                .isbn("9788576082675")
                .publisher("Alta Books")
                .publishedDate("2011")
                .pageCount(431)
                .category("Tecnologia")
                .price(new BigDecimal("89.90"))
                .quantityInStock(12)
                .description("Boas praticas de programacao para escrever codigo legivel e sustentavel.")
                .build());

        bookRepository.save(Book.builder()
                .title("O Hobbit")
                .author("J.R.R. Tolkien")
                .isbn("9788595084759")
                .publisher("HarperCollins")
                .publishedDate("2019")
                .pageCount(310)
                .category("Fantasia")
                .price(new BigDecimal("49.90"))
                .quantityInStock(20)
                .description("A jornada de Bilbo Bolseiro pela Terra-media.")
                .build());

        bookRepository.save(Book.builder()
                .title("Sapiens: Uma Breve Historia da Humanidade")
                .author("Yuval Noah Harari")
                .isbn("9788525432629")
                .publisher("L&PM")
                .publishedDate("2015")
                .pageCount(464)
                .category("Historia")
                .price(new BigDecimal("64.90"))
                .quantityInStock(8)
                .description("Como o Homo sapiens conquistou o mundo.")
                .build());
    }
}
