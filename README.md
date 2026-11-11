# Sistema de Produtos API

API RESTful desenvolvida com **Java 21** e **Spring Boot** para gerenciamento de produtos, categorias e controle de acesso com autenticação e autorização via **JWT (JSON Web Token)**.

O projeto foi construído seguindo as melhores práticas do ecossistema Spring: arquitetura em camadas, DTO Pattern com Java Records, validações declarativas com Bean Validation, mapeamento desacoplado de objetos, tratamento global de exceções, persistência relacional com Spring Data JPA/Hibernate, segurança stateless com Spring Security e documentação interativa via OpenAPI / Swagger.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21 (LTS)**
- **Spring Boot**
- **Spring Web MVC**
- **Spring Data JPA & Hibernate**
- **Spring Security** (Autenticação Stateless e RBAC)
- **JWT (io.jsonwebtoken / JJWT 0.12.6)**
- **Springdoc OpenAPI (Swagger UI)**
- **MySQL 8** (Banco de dados relacional)
- **Bean Validation (Hibernate Validator)**
- **Lombok**
- **Apache Maven** (com Maven Wrapper `./mvnw`)
- **Docker & Docker Compose**
- **Flyway Migration** (Controle e versionamento evolutivo do banco de dados)
- **JUnit 5 & Mockito** (Testes unitários automatizados)
- **GitHub Actions** (Pipeline de Integração Contínua - CI)

---

## 🚀 Funcionalidades

### 🔐 Autenticação e Usuários
- Cadastro de novos usuários com role padrão `ROLE_BASIC`.
- Login com credenciais e geração de token JWT assinado com chave HMAC.
- Proteção de senhas com hash criptográfico **BCrypt**.
- Controle de acesso baseado em papéis (**RBAC**): `ROLE_BASIC` e `ROLE_ADMIN`.

### 📦 Produtos
- Cadastro de produto com associação a uma ou mais categorias (*Apenas ADMIN*).
- Atualização completa de produto por ID (*Apenas ADMIN*).
- Exclusão de produto por ID (*Apenas ADMIN*).
- Listagem geral de produtos (com filtros opcionais por categoria, preço mínimo e máximo).
- Consulta de produto por ID.
- Consulta de produtos por nome (busca parcial/insensível a maiúsculas).
- Consulta de produtos por categoria (via Path Variable ou Query Param).
- Consulta com paginação configurável (`Pageable`).
- Consulta por faixa de preço com paginação (`precoMin` e `precoMax`).

### 🏷️ Categorias
- Criação de nova categoria (*Apenas ADMIN*).
- Listagem de todas as categorias cadastradas.
- Consulta de produtos vinculados a uma categoria específica por ID.

---

## 📐 Modelagem de Dados

### Entidades e Relacionamentos

```mermaid
erDiagram
    USUARIOS ||--|{ USUARIO_ROLES : possui
    ROLES ||--|{ USUARIO_ROLES : define
    PRODUTO }|--|{ CATEGORIA : pertence
    
    USUARIOS {
        bigint id PK
        varchar email UK
        varchar password
    }
    ROLES {
        bigint id PK
        varchar nome
    }
    PRODUTO {
        bigint id PK
        varchar nome
        decimal preco
    }
    CATEGORIA {
        bigint id PK
        varchar nome
    }
```

- **Produto ↔ Categoria**: Relacionamento `@ManyToMany` mapeado pela tabela de junção `produto_categoria`.
- **Usuario ↔ Roles**: Relacionamento `@ManyToMany` (`FetchType.EAGER`) mapeado pela tabela `usuario_roles`.
- **Preço**: Definido com `BigDecimal` (`precision = 19, scale = 2`) para garantir precisão monetária.

---

## 🗄️ Migrations do Banco de Dados (Flyway)

O gerenciamento e versionamento da estrutura do banco de dados é automatizado com o **Flyway**. As migrações ficam localizadas em `src/main/resources/db/migration/` e são aplicadas automaticamente ao iniciar a aplicação:

| Versão | Arquivo | Descrição |
| :--- | :--- | :--- |
| `V1` | `V1__create_tables_produto_e_categoria.sql` | Criação das tabelas `categoria`, `produto` e tabela de junção `produto_categoria` |
| `V2` | `V2__create_tables_roles_e_usuarios.sql` | Criação das tabelas `roles`, `usuarios` e tabela de junção `usuario_roles` |
| `V3` | `V3__seed_initial_roles.sql` | Carga inicial dos perfis de acesso padrão (`ROLE_BASIC` e `ROLE_ADMIN`) |

> [!NOTE]
> O Hibernate está configurado com `spring.jpa.hibernate.ddl-auto=validate`, garantindo que toda a evolução do banco de dados seja estritamente controlada via migrations versionadas pelo Flyway.

---

## 🔒 Regras de Segurança e Permissões

A API adota o modelo de segurança **Stateless** gerenciado pelo `SecurityConfiguration`:

| Rota / Endpoint | Método HTTP | Permissão Necessária |
| :--- | :--- | :--- |
| `/auth/register` | `POST` | Pública (Todos) |
| `/auth/login` | `POST` | Pública (Todos) |
| `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` | `GET` | Pública (Documentação) |
| `/produto` | `GET` | Pública (Todos) |
| `/categoria` | `GET` | Pública (Todos) |
| `/produto/{id}` | `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/produto/pagina`, `/produto/pagina/{page}` | `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/produto/nome/{nome}` | `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/produto/categoria`, `/produto/categoria/{categoria}` | `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/produto/faixa-preco`, `/produto/faixa-preco/{min}/{max}`| `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/categoria/{id}` | `GET` | Autenticado (`ROLE_BASIC` ou `ROLE_ADMIN`) |
| `/produto` | `POST` | Exclusivo `ROLE_ADMIN` |
| `/produto/{id}` | `PUT` | Exclusivo `ROLE_ADMIN` |
| `/produto/{id}` | `DELETE` | Exclusivo `ROLE_ADMIN` |
| `/categoria` | `POST` | Exclusivo `ROLE_ADMIN` |

> [!NOTE]
> Para acessar rotas autenticadas, informe o cabeçalho HTTP:
> `Authorization: Bearer <seu_token_jwt>`

---

## 📑 Documentação da API

### 1. Autenticação (`/auth`)

#### Registrar Usuário
- **POST** `/auth/register`
- **Body:**
```json
{
  "email": "usuario@exemplo.com",
  "password": "senhaSegura123"
}
```
- **Resposta (`201 Created`):**
```text
usuarioCriado
```

#### Realizar Login
- **POST** `/auth/login`
- **Body:**
```json
{
  "email": "usuario@exemplo.com",
  "password": "senhaSegura123"
}
```
- **Resposta (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

---

### 2. Produtos (`/produto`)

#### Criar Produto (Requer `ROLE_ADMIN`)
- **POST** `/produto`
- **Body:**
```json
{
  "nome": "Smartphone Galaxy",
  "preco": 1999.90,
  "categoriasIds": [1, 2]
}
```
- **Resposta (`200 OK`):**
```json
{
  "id": 1,
  "nome": "Smartphone Galaxy",
  "preco": 1999.90,
  "categorias": [
    "Eletrônicos",
    "Smartphones"
  ]
}
```

#### Listar Produtos com Filtros Opcionais
- **GET** `/produto?categoria=Eletronicos&precoMin=1000&precoMax=3000`

#### Listar Produtos Paginados
- **GET** `/produto/pagina?page=0&size=10&sort=id,asc`
- **GET** `/produto/pagina/1` (Página 1 com tamanho padrão de 10 itens)

#### Buscar por ID
- **GET** `/produto/{id}`

#### Buscar por Nome
- **GET** `/produto/nome/{nome}`

#### Buscar por Faixa de Preço (com paginação)
- **GET** `/produto/faixa-preco?precoMin=100.00&precoMax=500.00`
- **GET** `/produto/faixa-preco/100.00/500.00`

#### Atualizar Produto (Requer `ROLE_ADMIN`)
- **PUT** `/produto/{id}`
- **Body:**
```json
{
  "nome": "Smartphone Galaxy Pro",
  "preco": 2299.90,
  "categoriasIds": [1]
}
```

#### Deletar Produto (Requer `ROLE_ADMIN`)
- **DELETE** `/produto/{id}`
- **Resposta:** `204 No Content`

---

### 3. Categorias (`/categoria`)

#### Criar Categoria (Requer `ROLE_ADMIN`)
- **POST** `/categoria`
- **Body:**
```json
{
  "nome": "Informática"
}
```
- **Resposta (`200 OK`):**
```json
{
  "id": 1,
  "nome": "Informática"
}
```

#### Listar Categorias
- **GET** `/categoria`
- **Resposta (`200 OK`):**
```json
[
  {
    "id": 1,
    "nome": "Informática"
  }
]
```

#### Consultar Produtos de uma Categoria
- **GET** `/categoria/{id}`
- **Resposta (`200 OK`):**
```json
[
  {
    "id": 1,
    "nome": "Notebook Dell",
    "preco": 3500.00
  }
]
```

---

## 📖 Swagger / OpenAPI

Com a aplicação em execução, a documentação interativa e testes de endpoints ficam disponíveis em:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Docs:** `http://localhost:8080/v3/api-docs`

---

## 💻 Como Executar o Projeto

### Pré-requisitos
- **Docker** e **Docker Compose** instalados.
- **Java 21 (JDK)** (apenas se for executar localmente fora do container).
- **Maven** (opcional, o repositório inclui o Maven Wrapper `./mvnw`).

---

### 1. Configurar as Variáveis de Ambiente

Antes de iniciar os containers ou a aplicação, crie o seu arquivo `.env` a partir do modelo `.env.example`:

```bash
cp .env.example .env
```

> [!TIP]
> O arquivo `.env` já vem pré-configurado com valores padrões funcionais para testes locais. Altere as senhas e a chave `JWT_KEY` conforme sua necessidade. O `.env` é ignorado pelo Git para proteger suas credenciais.

---

### 2. Escolha como executar a aplicação

> [!IMPORTANT]
> Escolha **uma** das opções abaixo para executar a API:
> - **Opção 1**: Executa **tudo no Docker** (API + MySQL) usando o Dockerfile multistage e compose.
> - **Opção 2**: Executa **apenas o MySQL no Docker** e roda a API localmente via `./mvnw spring-boot:run` (ideal para desenvolvimento diário e debug rápido).
> 
> Não execute as duas simultaneamente para evitar conflito na porta `8080`.

---

### Opção 1: Rodar tudo conteinerizado (API + MySQL) via Docker Compose

Esta é a opção recomendada para testar o ambiente idêntico a produção:

1. **Construa as imagens e inicie todos os serviços em segundo plano:**
   ```bash
   docker compose up --build -d
   ```
2. **Acompanhe os logs da aplicação:**
   ```bash
   docker compose logs -f api
   ```
   *(Pressione `Ctrl + C` para sair dos logs sem parar os containers).*

3. **Verificar o status dos serviços:**
   ```bash
   docker compose ps
   ```

4. **Para parar todos os containers:**
   ```bash
   docker compose down
   ```
   *(Para parar e apagar também o volume persistente do banco, use `docker compose down -v`).*

---

### Opção 2: Iniciar apenas o Banco (MySQL) e rodar a API localmente

Ideal para quem deseja debugar breakpoints na IDE ou alterar código com recarga rápida:

#### Passo 1: Iniciar apenas o MySQL no Docker
```bash
docker compose up -d mysql
```

#### Passo 2: Executar a Aplicação Spring Boot localmente
Exporte as variáveis ou execute com o Maven Wrapper:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

A API iniciará na porta padrão `8080` (`http://localhost:8080`).

> Para interromper o container do banco quando terminar:
> ```bash
> docker compose down
> ```

---

### Passo 3: Executar a Suíte de Testes Automatizados

O projeto conta com testes unitários cobrindo as regras de negócio dos serviços com **JUnit 5** e **Mockito**:

```bash
./mvnw test
```

---

## ⚙️ Variáveis de Ambiente e Configurações

As configurações do sistema residem em `src/main/resources/application.properties` e são alimentadas dinamicamente pelo arquivo `.env`:

| Variável (.env) | Descrição | Exemplo Padrão |
| :--- | :--- | :--- |
| `MYSQL_DATABASE` | Nome do banco/schema MySQL | `produtosapi` |
| `MYSQL_USER` | Usuário comum criado para a aplicação | `appuser` |
| `MYSQL_PASSWORD` | Senha do usuário da aplicação | `123` |
| `MYSQL_ROOT_PASSWORD` | Senha administrativa do MySQL | `123` |
| `JWT_KEY` | Chave secreta de assinatura HMAC do token JWT | Chave HMAC de 256+ bits |
| `JWT_EXPIRATION` | Tempo de expiração do token em milissegundos | `3600000` (1 hora) |

---

## 🏗️ Estrutura do Projeto

```text
produtos-api/
├── .github/
│   └── workflows/
│       └── ci.yml               # Pipeline de CI (Build e Test no GitHub Actions)
├── .env.example                 # Template de variáveis de ambiente
├── docker-compose.yml           # Orquestração dos containers (API + MySQL)
├── Dockerfile                   # Build multistage (Eclipse Temurin 21 JDK/JRE)
├── src/
│   ├── main/
│   │   ├── java/com/bruno/sistemaProdutos/
│   │   │   ├── config/          # Filtros e configurações de segurança e JWT
│   │   │   ├── controller/      # Controladores REST expostos pela API
│   │   │   ├── dto/             # Records de entrada/saída (Requests e Responses)
│   │   │   ├── entity/          # Entidades JPA (Produto, Categoria, Usuario, Roles)
│   │   │   ├── enums/           # Enums do domínio (Role: ROLE_BASIC, ROLE_ADMIN)
│   │   │   ├── exception/       # Exceções customizadas e payload de erros
│   │   │   ├── handler/         # GlobalExceptionHandler (@ControllerAdvice)
│   │   │   ├── mapper/          # Conversores entre entidades e DTOs
│   │   │   ├── repository/      # Interfaces de persistência Spring Data JPA
│   │   │   ├── service/         # Camada de lógica de negócio e autenticação
│   │   │   └── SistemaProdutosApplication.java
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/   # Migrações versionadas do Flyway (V1, V2, V3)
│   │       └── application.properties
│   └── test/
│       └── java/com/bruno/sistemaProdutos/
│           ├── service/         # Testes unitários com JUnit 5 e Mockito
│           └── SistemaProdutosApplicationTests.java
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🎯 Próximas Evoluções

- [x] Criação de `Dockerfile` multi-stage para containerização completa da API.
- [x] Orquestração completa da API junto ao banco de dados no Docker Compose com variáveis de ambiente.
- [ ] Maior cobertura de testes unitários.
- [ ] Testes de integração de ponta a ponta com **MockMvc** e **Testcontainers**.
- [ ] Implementação de pipeline de Continuous Deployment (CD).
- [ ] Cache distribuído com Redis para consultas frequentes de produtos e categorias.
