# CarrinhoColetivo 🛒

O **CarrinhoColetivo** é uma plataforma focada na gestão de compras inteligentes e compartilhamento de preços, desenvolvida como projeto da disciplina de Técnicas de Programação 2. O projeto visa facilitar a organização de listas de compras e auxiliar a comunidade a encontrar os melhores preços.

## Tecnologias Utilizadas

### Backend
* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.3.0
* **Banco de Dados:** PostgreSQL
* **Persistência:** Spring Data JPA / Hibernate
* **Migrações:** Flyway
* **Segurança:** Spring Security (BCrypt)
* **Testes:** JUnit 5, MockMvc

### Frontend
* **Linguagem:** HTML5, CSS3, JavaScript
* **Hospedagem:** Vercel

---

## Arquitetura do Projeto

O projeto segue uma arquitetura baseada em serviços separados:
1. **Frontend (Vercel):** Responsável pela interface do usuário e consumo da API.
2. **Backend (Render):** API RESTful que gerencia as regras de negócio, persistência de dados e segurança.

---

## Requisitos Implementados

* **EU001:** Cadastro de usuário com validação de dados e criptografia de senha (BCrypt).
* **EU002:** Login seguro com verificação de credenciais e gerenciamento de sessão via `localStorage`.

---

## Estratégia de Testes (TDD)

Este projeto foi desenvolvido utilizando a metodologia **TDD (Test-Driven Development)**. A cobertura de testes de integração garante a integridade das rotas da API e o comportamento correto das regras de negócio.

* **Testes de Integração:** Utilização de `@WebMvcTest` e `MockMvc` para validar o comportamento dos controllers sem depender de infraestrutura externa.
* **Cobertura:** O projeto visa atingir >80% de cobertura de código.
* **Execução:** Para rodar os testes, utilize no terminal:
    ```bash
    ./mvnw test
    ```

---

## Como Executar

### Pré-requisitos
* Java 17 ou superior
* Maven
* PostgreSQL rodando localmente (ou via Docker)

### Backend (Local)
1. Clone o repositório.
2. Configure as variáveis de ambiente no `application.properties` (ou variáveis de sistema):
   * `DB_URL`: `jdbc:postgresql://localhost:5432/compras_db`
   * `DB_USER`: `[seu_usuario]`
   * `DB_PASSWORD`: `[sua_senha]`
3. Execute a classe `ComprasApplication.java`.

### Frontend
1. Acesse o repositório: [Repositório Front-End](https://github.com/davilb64/front-tp2)
1. Abra os arquivos `.html` diretamente em um navegador moderno (ou utilize o *Live Server* do VS Code).
2. Certifique-se de que a URL do `fetch` no JavaScript esteja apontando para o endereço correto da API (Local ou Produção).

---

## Deploy
* **Aplicação funcional:** [CarrinhoColetivo](https://front-tp2.vercel.app/)
* **Backend:** Publicado na plataforma [Render](https://render.com).
* **Frontend:** Publicado na plataforma [Vercel](https://vercel.com).

---

## 👥 Desenvolvedores

* Davi Lopes - Integrador (Front e Back-End) / Scrum Master
* João Vitor Lopes - Back-End
* Letícia Pimentel - Back-End e Banco de Dados
* Rômulo Uriel - Back-End
* Valquíria Machado - Front-End / Dona do Produto

---

> *Projeto desenvolvido para a disciplina de Técnicas de Programação 2 - Universidade de Brasília (UnB).*
