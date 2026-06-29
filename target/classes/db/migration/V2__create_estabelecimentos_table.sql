-- Migration V2: Tabela de estabelecimentos
-- EU003: Eu como usuário/admin quero poder criar, editar e deletar estabelecimentos.

CREATE TABLE estabelecimentos (
    id         BIGSERIAL    PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    endereco   VARCHAR(255),
    criado_em  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_estabelecimentos_nome ON estabelecimentos (nome);
