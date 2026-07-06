-- Migration V3: Tabela de produtos
-- EU004: Eu como administrador quero poder adicionar e aprovar produtos.
-- EU005: Eu como usuário quero poder submeter produtos para aprovação do administrador.
-- EU006: Eu como usuário/admin quero iniciar o cadastro pelo código de barras.
-- EU007: Eu como usuário/admin quero receber aviso se o produto já existe.
-- RQ011: O administrador pode aprovar ou rejeitar produtos com status PENDENTE (campo status).
-- RQ012: Produtos APROVADOS ficam visíveis no catálogo público; PENDENTES e REJEITADOS não.
-- RQ013: Usuários podem submeter novos produtos com status inicial PENDENTE (DEFAULT 'PENDENTE').
-- RQ014: Produtos PENDENTES não devem aparecer em buscas públicas.
-- RQ015: O código de barras deve ser identificador único de produto (UNIQUE em codigo_barras).
-- RQ017: O sistema deve detectar duplicata ao tentar cadastrar produto com código de barras já existente.
-- RQ024: O sistema deve permitir busca de produtos por nome parcial (índice idx_produtos_nome).
-- RQ025: O sistema deve permitir filtro de produtos por categoria (campo categoria).

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
