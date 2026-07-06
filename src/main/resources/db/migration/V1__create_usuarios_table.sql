-- Migration V1: Tabela de usuários
-- EU001: Eu como usuário quero poder criar uma conta no site para poder interagir.
-- EU002: Eu como usuário quero poder fazer login no site se já possuo uma conta e cair na tela principal.
-- RQ001: O sistema deve permitir cadastro de novo usuário informando nome, email e senha.
-- RQ002: O email deve ser único no sistema (restrição UNIQUE na coluna email).
-- RQ003: A senha deve ser armazenada como hash BCrypt, nunca em texto plano (coluna senha_hash).
-- RQ004: O papel padrão do usuário deve ser USUARIO ao se cadastrar (DEFAULT 'USUARIO').
-- RQ005: O sistema deve autenticar o usuário por email e senha (índice idx_usuarios_email para busca).
-- RQ006: O sistema deve retornar um token JWT após autenticação bem-sucedida.
-- RQ007: Credenciais inválidas devem resultar em erro genérico sem revelar qual campo falhou.

CREATE TYPE papel_enum AS ENUM ('USUARIO', 'ADMINISTRADOR');

CREATE TABLE usuarios (
                          id          BIGSERIAL       PRIMARY KEY,
                          nome        VARCHAR(100)    NOT NULL,
                          email       VARCHAR(150)    NOT NULL UNIQUE,
                          senha_hash  VARCHAR(255)    NOT NULL,
                          papel       papel_enum      NOT NULL DEFAULT 'USUARIO',
                          criado_em   TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Índice para buscas por e-mail no login
CREATE INDEX idx_usuarios_email ON usuarios (email);
