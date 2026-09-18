# 📚 Livraria API

API REST em **Java + Spring Boot** para gerenciar o catálogo de uma livraria, com integração à **Google Books API**: basta informar o ISBN de um livro e o sistema busca automaticamente título, autor, editora, capa e descrição, já salvando no catálogo.

> Projeto de portfólio construído para praticar arquitetura em camadas, consumo de API externa e boas práticas com Spring Boot.

---

## ✨ Funcionalidades

- CRUD completo de livros (criar, listar, buscar, atualizar, remover)
- Busca por título ou categoria
- **Importação automática via ISBN**: consome a Google Books API e popula os dados do livro
- Validação de dados de entrada (Bean Validation)
- Tratamento centralizado de erros, com respostas JSON padronizadas
- Documentação interativa via Swagger/OpenAPI
- Banco de dados em memória (H2) — roda sem precisar instalar nada
- Dados de exemplo carregados automaticamente ao subir a aplicação
- Testes unitários da camada de serviço e de integração da camada web (JUnit 5, Mockito, MockMvc)

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistência | Spring Data JPA + H2 |
| Integração externa | Google Books API (via `RestTemplate`) |
| Validação | Bean Validation (Jakarta) |
| Documentação | springdoc-openapi / Swagger UI |
| Testes | JUnit 5, Mockito, AssertJ |
| Build | Maven |
| Boilerplate | Lombok |
| Containerização | Docker + Docker Compose (perfil `docker` com PostgreSQL) |

---

## 🏗️ Arquitetura

Organizado em camadas, separando responsabilidades:

```
controller/  → recebe as requisições HTTP e devolve as respostas
service/     → regras de negócio (BookService e GoogleBooksService)
repository/  → acesso a dados (Spring Data JPA)
model/       → entidade JPA (Book)
dto/         → objetos de entrada/saída da API + mapper
exception/   → exceções customizadas + handler global
config/      → beans de configuração (RestTemplate, OpenAPI)
runner/      → seed de dados iniciais
```

---

## 🚀 Como rodar o projeto

Pré-requisitos: **Java 17** e **Maven** instalados.

```bash
# Clonar o repositório
git clone https://github.com/SEU-USUARIO/livraria-api.git
cd livraria-api

# Rodar a aplicação
mvn spring-boot:run
```

> Dica: se você usa IntelliJ ou VS Code com extensão Java, também pode simplesmente abrir a pasta do projeto e rodar `LivrariaApiApplication.java` direto pela IDE.

A API sobe em `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Console do banco H2: `http://localhost:8080/h2-console`
  (JDBC URL: `jdbc:h2:mem:livraria`, usuário `sa`, sem senha)

Ao iniciar, a aplicação já cadastra 3 livros de exemplo, então dá pra testar os endpoints de listagem/busca imediatamente.

---

## 🐳 Rodando com Docker

Sobe a aplicação já com PostgreSQL, sem precisar instalar nada localmente:

```bash
docker compose up --build
```

Isso levanta dois containers:
- `livraria-api-db` — PostgreSQL 16
- `livraria-api` — a aplicação, rodando com o profile `docker` (troca o H2 pelo Postgres automaticamente)

A API sobe em `http://localhost:8080`, com o mesmo Swagger de sempre em `http://localhost:8080/swagger-ui.html`.

```bash
# Derrubar os containers
docker compose down

# Derrubar e também apagar os dados do banco
docker compose down -v
```

> Sem Docker, o projeto continua rodando normalmente com `mvn spring-boot:run` (H2 em memória, como descrito acima).

---

## 📖 Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/books` | Lista todos os livros |
| GET | `/api/books?title=hobbit` | Filtra por título |
| GET | `/api/books?category=Tecnologia` | Filtra por categoria |
| GET | `/api/books/{id}` | Busca um livro pelo id |
| POST | `/api/books` | Cadastra um livro manualmente |
| POST | `/api/books/import/{isbn}` | **Busca na Google Books API pelo ISBN e cadastra automaticamente** |
| PUT | `/api/books/{id}` | Atualiza um livro existente |
| DELETE | `/api/books/{id}` | Remove um livro |

### Exemplo: importar um livro pelo ISBN

```bash
curl -X POST http://localhost:8080/api/books/import/9780134685991
```

A API busca o ISBN na Google Books, extrai os dados e devolve o livro já criado:

```json
{
  "id": 4,
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isbn": "9780134685991",
  "publisher": "Addison-Wesley",
  "publishedDate": "2018",
  "category": "Computers",
  "price": 39.90,
  "quantityInStock": 1,
  "importedFromExternalApi": true,
  "createdAt": "2026-08-07T10:15:30"
}
```

### Exemplo: cadastrar um livro manualmente

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Domain-Driven Design",
    "author": "Eric Evans",
    "isbn": "9780321125217",
    "publisher": "Addison-Wesley",
    "price": 99.90,
    "quantityInStock": 3,
    "category": "Tecnologia"
  }'
```

---

## 🧪 Rodando os testes

```bash
mvn test
```

---

## 💡 Possíveis evoluções

- Paginação e ordenação nos endpoints de listagem
- Autenticação com Spring Security + JWT
- Cache para as consultas à Google Books API
- Pipeline de CI/CD (GitHub Actions) para build e testes automáticos
- Deploy em nuvem (Oracle Cloud / Render / Railway)

---

## 👩‍💻 Autora

Desenvolvido por **Gicélia** como projeto de portfólio, aplicando conceitos estudados em Java, Spring Boot e integração de APIs REST.

## 📄 Licença

Este projeto está sob a licença MIT — sinta-se à vontade para usar como referência de estudo.
