-- Migration V4: Tabela de variações de produto
-- EU006: Eu como usuário/admin quero iniciar o cadastro pelo código de barras.
-- EU007: Eu como usuário/admin quero receber aviso se o produto já existe.
-- RQ015: Cada variação possui código de barras único (UNIQUE em codigo_barras).
-- RQ016: O sistema deve permitir busca de variação por código de barras (índice idx_variacoes_codigo_barras).
-- RQ017: O sistema deve detectar duplicata de variação pelo código de barras antes de persistir.
-- Cada variação representa um SKU específico (ex: "Arroz 1kg", "Arroz 5kg") do mesmo produto pai.

CREATE TABLE variacoes_produto (
    id            BIGSERIAL    PRIMARY KEY,
    produto_id    BIGINT       NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    descricao     VARCHAR(100) NOT NULL,
    peso          DECIMAL(10,3),
    unidade       VARCHAR(20),
    -- código de barras próprio da variação (EU005)
    codigo_barras VARCHAR(50)  UNIQUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_variacoes_produto_id    ON variacoes_produto (produto_id);
CREATE INDEX idx_variacoes_codigo_barras ON variacoes_produto (codigo_barras);
