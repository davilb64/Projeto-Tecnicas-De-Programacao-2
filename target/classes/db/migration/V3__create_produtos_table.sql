-- Migration V3: Tabela de produtos
-- EU004: Eu como usuário/admin quero poder criar, editar e deletar produtos.
-- EU005: Eu como usuário/admin quero iniciar o cadastro de um produto pelo código de barras.
-- EU006: Eu como usuário/admin quero receber aviso se tentar cadastrar um produto já existente.

CREATE TYPE status_produto_enum AS ENUM ('PENDENTE', 'APROVADO', 'REJEITADO');

CREATE TABLE produtos (
    id            BIGSERIAL           PRIMARY KEY,
    nome          VARCHAR(150)        NOT NULL,
    -- código de barras único: permite detectar duplicatas (EU005, EU006)
    codigo_barras VARCHAR(50)         UNIQUE,
    descricao     TEXT,
    categoria     VARCHAR(100),
    status        status_produto_enum NOT NULL DEFAULT 'PENDENTE',
    criado_por    BIGINT              REFERENCES usuarios(id) ON DELETE SET NULL,
    criado_em     TIMESTAMP           NOT NULL DEFAULT NOW()
);

-- Índice para busca rápida por código de barras (EU005)
CREATE INDEX idx_produtos_codigo_barras ON produtos (codigo_barras);
CREATE INDEX idx_produtos_nome          ON produtos (nome);
