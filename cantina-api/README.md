# Cantina API

API REST desenvolvida em **Java + Spring Boot + MySQL** para o gerenciamento do catálogo de lanches de uma cantina escolar, dando continuidade ao sistema de caixa desenvolvido anteriormente (SA 1). O objetivo é permitir que o cardápio (cadastro, listagem, consulta, atualização e remoção de lanches) seja mantido de forma dinâmica, sem depender de alterações no código-fonte.

## Objetivo

Disponibilizar uma API REST que sirva de base para que o sistema de caixa da cantina consuma as informações dos produtos de forma dinâmica, aplicando boas práticas de arquitetura em camadas, código limpo, validação de dados e tratamento de erros.

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.3
- Spring Web (REST)
- Spring Data JPA
- Bean Validation (Jakarta Validation)
- MySQL 8
- Lombok
- Maven

## Arquitetura

O projeto segue o padrão REST com separação em camadas:

```
src/main/java/com/senai/cantina/
├── controller/     -> Endpoints HTTP (LancheController)
├── service/        -> Regras de negócio (LancheService)
├── repository/     -> Acesso ao banco de dados (LancheRepository)
├── model/          -> Entidade JPA (Lanche)
├── dto/            -> Objetos de entrada/saída (Request, Response, Resumo, Error)
└── exception/      -> Exceções customizadas e tratamento global de erros
```

- O **Controller** não contém regra de negócio: apenas recebe a requisição, delega ao Service e devolve a resposta HTTP.
- O **Service** concentra as regras de negócio e é o único ponto de conversão entre entidade e DTO.
- O **Repository** é a única camada que conversa com o banco de dados (via Spring Data JPA).
- **Entidade** e **DTO** são separados: a API nunca expõe a entidade JPA diretamente.

## Modelo de dados — Lanche

| Campo       | Tipo       | Obrigatório | Observação                          |
|-------------|------------|-------------|--------------------------------------|
| id          | Long       | gerado automaticamente | Identificador único |
| nome        | String     | sim         | Máx. 100 caracteres |
| descricao   | String     | não         | Máx. 255 caracteres |
| preco       | BigDecimal | sim         | Deve ser maior que zero |
| categoria   | String     | não         | Ex.: "Salgado", "Doce", "Bebida" |
| disponivel  | Boolean    | sim         | Indica se o lanche está disponível para venda |

## Como executar

### Pré-requisitos

- JDK 21+
- Maven 3.9+ (ou usar o wrapper, se adicionado ao repositório)
- MySQL 8 em execução

### 1. Criar o banco de dados

Não é necessário criar o schema manualmente — a aplicação cria o banco `cantina_db` automaticamente na primeira execução (`createDatabaseIfNotExist=true`). Basta que o MySQL esteja rodando e acessível.

### 2. Configurar credenciais

Edite `src/main/resources/application.properties` com as credenciais do seu MySQL local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cantina_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Executar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

### 4. Rodar os testes

```bash
mvn test
```

## Endpoints

Base URL: `http://localhost:8080/api/lanches`

| Verbo  | Endpoint             | Descrição                                   | Status de sucesso |
|--------|-----------------------|----------------------------------------------|--------------------|
| POST   | `/api/lanches`        | Cadastra um novo lanche                       | `201 Created`      |
| GET    | `/api/lanches`        | Lista todos os lanches (apenas nome e preço)  | `200 OK`           |
| GET    | `/api/lanches/{id}`   | Consulta um lanche por ID (todos os campos)   | `200 OK`           |
| PUT    | `/api/lanches/{id}`   | Atualiza um lanche existente                  | `200 OK`           |
| DELETE | `/api/lanches/{id}`   | Remove um lanche                              | `204 No Content`   |

Quando o `id` informado não existe, `GET`, `PUT` e `DELETE` retornam `404 Not Found`. Quando os dados enviados são inválidos, `POST` e `PUT` retornam `400 Bad Request`.

### Exemplo — Cadastrar lanche

`POST /api/lanches`

```json
{
  "nome": "Coxinha de Frango",
  "descricao": "Coxinha tradicional recheada com frango desfiado",
  "preco": 7.50,
  "categoria": "Salgado",
  "disponivel": true
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "nome": "Coxinha de Frango",
  "descricao": "Coxinha tradicional recheada com frango desfiado",
  "preco": 7.50,
  "categoria": "Salgado",
  "disponivel": true
}
```

### Exemplo — Listar todos os lanches

`GET /api/lanches`

Resposta `200 OK`:

```json
[
  { "nome": "Coxinha de Frango", "preco": 7.50 },
  { "nome": "Suco de Laranja", "preco": 5.00 }
]
```

### Exemplo — Consultar por ID (não encontrado)

`GET /api/lanches/99`

Resposta `404 Not Found`:

```json
{
  "timestamp": "2026-08-24T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Lanche não encontrado com o ID: 99",
  "path": "/api/lanches/99"
}
```

### Exemplo — Erro de validação

`POST /api/lanches` com corpo inválido (`nome` vazio e `preco` negativo):

Resposta `400 Bad Request`:

```json
{
  "timestamp": "2026-08-24T10:16:02",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos dados enviados",
  "path": "/api/lanches",
  "details": [
    "nome: O nome do lanche é obrigatório",
    "preco: O preço deve ser maior que zero"
  ]
}
```

### Exemplo — Remover lanche

`DELETE /api/lanches/1` → `204 No Content` (sem corpo na resposta)

## Tratamento de erros

Todos os erros da API seguem o mesmo formato JSON, centralizado em `GlobalExceptionHandler`:

- `ResourceNotFoundException` → `404 Not Found`
- Falha de validação (`@Valid`) → `400 Bad Request`, com detalhamento por campo
- JSON malformado / corpo ausente → `400 Bad Request`
- Qualquer outra exceção não tratada → `500 Internal Server Error`

## Próximos passos

- Integrar o sistema de caixa (SA 1) para consumir esta API dinamicamente.
- Adicionar paginação na listagem geral.
- Adicionar testes de integração para o Controller (MockMvc).

---


