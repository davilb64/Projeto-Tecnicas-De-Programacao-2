-- Migration V1: Tabela de usuários
-- EU001: Eu como usuário quero poder criar uma conta no site para poder interagir.
-- EU002: Eu como usuário quero poder fazer login no site se já possuo uma conta e cair na tela principal.

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
