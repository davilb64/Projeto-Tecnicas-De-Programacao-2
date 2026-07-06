# CarrinhoColetivo

O **CarrinhoColetivo** é uma plataforma de gerência de compras e compartilhamento de preços, desenvolvida como projeto da disciplina de Técnicas de Programação 2 — Universidade de Brasília, 1/2026.

## Integrantes

| Nome | Matrícula | Responsabilidade |
|------|-----------|-----------------|
| Davi Lopes Brito | 242023425 | Integrador (Front e Back-End) / Scrum Master |
| João Vitor Lopes Rocha | 242014041 | Back-End |
| Letícia Pimentel Garcia | 221016204 | Back-End e Banco de Dados |
| Rômulo Uriel Liberal de Oliveira | 242003880 | Back-End |
| Valquíria dos Santos Machado | 242003807 | Front-End / Dona do Produto |

---

## Tecnologias Utilizadas

### Backend
- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.3.0
- **Banco de Dados:** PostgreSQL 15+
- **Persistência:** Spring Data JPA / Hibernate
- **Migrações:** Flyway
- **Segurança:** Spring Security + JWT (Auth0 java-jwt 4.4.0)
- **Testes:** JUnit 5 + Mockito
- **Cobertura:** JaCoCo (mínimo 80%)
- **Análise Estática:** SpotBugs
- **Padrão de Código:** Google Java Style Guide (Checkstyle)
- **Documentação:** maven-javadoc-plugin (estilo Javadoc/Doxygen)
- **Build:** Maven 3.9+
- **Deploy:** Docker

### Frontend
- **Linguagem:** HTML5, CSS3, JavaScript
- **Hospedagem:** Vercel

Todas as bibliotecas utilizam licenças aprovadas: Apache License 2.0, MIT, EPL 2.0 / MPL 2.0, BSD.

---

## Arquitetura

```
src/main/java/com/tp2/compras/
  model/       → Entidades JPA (Usuario, Produto, Estabelecimento, ...)
  repository/  → Repositórios Spring Data JPA
  service/     → Regras de negócio
  controller/  → Endpoints REST
  dto/         → Objetos de transferência de dados
  config/      → Configurações (Security, JWT)
  exception/   → Exceções customizadas
  infra/       → Infraestrutura (filtros de segurança, TokenService)
src/main/resources/
  application.properties       → Configurações da aplicação
  db/migration/                → Migrações Flyway (V1–V5)
src/test/java/com/tp2/compras/ → Testes automatizados (JUnit 5)
```

---

## Pré-requisitos

- Java 17+ ([Adoptium](https://adoptium.net/))
- Maven 3.9+
- PostgreSQL 15+ **ou** Docker

---

## Configuração do Banco de Dados

Crie o banco antes de executar (apenas na primeira vez):

```sql
psql -U postgres
CREATE DATABASE compras_db;
\q
```

A aplicação usa as variáveis de ambiente abaixo (valores padrão entre parênteses):

| Variável | Padrão |
|----------|--------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/compras_db` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | *(sua senha local do PostgreSQL)* |
| `JWT_SECRET` | `local-pass` |

> **Atenção:** a senha do banco é pessoal e não deve ser compartilhada nem commitada.

As tabelas são criadas automaticamente pelo Flyway na primeira inicialização.

---

## Como Compilar e Testar

```bash
# Compilar
mvn clean compile

# Rodar testes (usa H2 em memória — não precisa do PostgreSQL)
mvn clean test

# Relatório de cobertura (JaCoCo) → target/site/jacoco/index.html
mvn clean test

# Análise estática (SpotBugs)
mvn spotbugs:check

# Gerar documentação Javadoc → target/site/apidocs/index.html
mvn javadoc:javadoc

# Gerar JAR executável
mvn clean package -DskipTests
```

---

## Como Executar

### Local (sem Docker)

```bash
# Com variável de ambiente
DB_PASSWORD=suasenha mvn spring-boot:run

# Usando o padrão (senha 'postgres')
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### Via Docker

```bash
docker build -t compras-api .

docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/compras_db \
  -e DB_USER=postgres \
  -e DB_PASSWORD=suasenha \
  -e JWT_SECRET=sua-chave-secreta \
  compras-api
```

---

## Endpoints da API

Base URL: `http://localhost:8080`

| Método | Rota | Descrição | EU |
|--------|------|-----------|-----|
| POST | `/api/usuarios/cadastro` | Cadastrar novo usuário | EU001 |
| POST | `/api/usuarios/login` | Autenticar e obter token JWT | EU002 |
| GET | `/api/usuarios/{id}` | Buscar usuário por ID | EU001 |
| PUT | `/api/usuarios/{id}` | Atualizar dados do usuário | EU001 |
| DELETE | `/api/usuarios/{id}` | Remover usuário | EU001 |
| GET | `/api/usuarios/status` | Health check da API | — |
| POST | `/api/estabelecimentos/cadastro` | Cadastrar estabelecimento | EU004 |
| GET | `/api/estabelecimentos` | Listar estabelecimentos | EU010 |
| GET | `/api/estabelecimentos/{id}` | Buscar estabelecimento | EU010 |
| PUT | `/api/estabelecimentos/{id}` | Atualizar estabelecimento | EU004 |
| DELETE | `/api/estabelecimentos/{id}` | Remover estabelecimento | EU004 |
| POST | `/api/produtos/cadastro` | Cadastrar produto | EU004, EU006 |
| GET | `/api/produtos` | Listar produtos | EU010 |
| GET | `/api/produtos/pendentes` | Produtos pendentes de aprovação | EU005 |
| GET | `/api/produtos/{id}` | Buscar produto | EU010 |
| PUT | `/api/produtos/{id}` | Atualizar produto | EU004 |
| DELETE | `/api/produtos/{id}` | Remover produto | EU004 |
| POST | `/api/variacoes/cadastro` | Cadastrar variação de produto | EU006 |
| GET | `/api/variacoes/produto/{id}` | Listar variações de um produto | EU006 |
| POST | `/api/listas` | Criar lista de compras | EU008 |
| GET | `/api/listas` | Listar listas do usuário | EU008 |
| PUT | `/api/listas/{id}` | Atualizar lista | EU009 |
| DELETE | `/api/listas/{listaId}` | Remover lista | EU008 |
| POST | `/api/listas/{listaId}/itens` | Adicionar item à lista | EU009 |
| DELETE | `/api/listas/itens/{itemId}` | Remover item da lista | EU009 |
| GET | `/api/listas/{listaId}/comparacao` | Comparar preços por mercado | EU011 |
| POST | `/api/precos/solicitar` | Registrar preço em estabelecimento | EU011 |
| GET | `/api/precos/historico` | Histórico de preços | EU011 |

---

## Estratégia de Testes (TDD)

O projeto foi desenvolvido com TDD. Os testes existem para:

- `UsuarioRepositoryTest` — EU001, EU002
- `EstabelecimentoRepositoryTest` — EU004, EU007
- `ProdutoRepositoryTest` — EU004–EU007
- `ListaComprasRepositoryTest` — EU008, EU009
- `UsuarioServiceTest` — EU001, EU002

Os testes usam banco H2 em memória com `MODE=PostgreSQL`, sem necessidade de PostgreSQL instalado.

---

## Verificador Dinâmico (equivalente ao Valgrind)

A JVM possui gerenciamento automático de memória via Garbage Collector, eliminando a classe de erros que o Valgrind detecta em C/C++. O equivalente funcional é o **VisualVM**, incluído no JDK:

```bash
# 1. Inicie o VisualVM
jvisualvm

# 2. Em outro terminal, execute a aplicação
mvn spring-boot:run

# 3. No VisualVM: selecione o processo "compras" e monitore
#    Heap Usage, Threads, CPU e Garbage Collector
```

---

## Rastreamento de Requisitos

As tags `EU001`–`EU017` presentes no código rastreiam cada funcionalidade até sua estória de usuário. O arquivo [`rastreamento_requisitos.txt`](rastreamento_requisitos.txt) mapeia cada EU aos artefatos de banco, entidade, repositório, serviço, controller, testes e diagramas do Trabalho 3.

---

## Arquivos Relevantes

| Arquivo | Descrição |
|---------|-----------|
| `pom.xml` | Configuração do build Maven |
| `Dockerfile` | Imagem Docker da aplicação |
| `rastreamento_requisitos.txt` | Matriz de rastreamento EU001–EU017 |
| `laudo_revisao_codigo.txt` | Laudo de revisão de código (checklist) |
| `Relatórios Individuais/` | Relatórios de horas por membro |
| `docs/` | Diagramas e PDFs do projeto |
| `src/main/resources/db/migration/` | Migrações Flyway V1–V5 |

---

## Deploy

- **Aplicação:** [CarrinhoColetivo](https://front-tp2.vercel.app/)
- **Backend:** Publicado no [Render](https://render.com)
- **Frontend:** Publicado no [Vercel](https://vercel.com)
- **Repositório Frontend:** [davilb64/front-tp2](https://github.com/davilb64/front-tp2)
