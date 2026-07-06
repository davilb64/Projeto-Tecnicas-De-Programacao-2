-- Migration V2: Tabela de estabelecimentos
-- EU004: Eu como administrador quero poder adicionar novos estabelecimentos.
-- RQ010: O sistema deve permitir cadastro de estabelecimentos com nome, endereço e geolocalização.
-- RQ018: O sistema deve retornar erro de conflito ao tentar cadastrar estabelecimento com nome já existente.
-- RQ026: O sistema deve permitir busca de estabelecimentos por nome parcial (índice idx_estabelecimentos_nome).

CREATE TABLE estabelecimentos (
    id         BIGSERIAL    PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    endereco   VARCHAR(255),
    criado_em  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_estabelecimentos_nome ON estabelecimentos (nome);
