-- Migration V5: Tabelas de listas de compras e itens
-- EU008: Eu como usuário quero poder criar uma lista de compras em branco.
-- EU009: Eu como usuário quero poder modificar uma lista de compras já criada.
-- RQ019: O usuário pode criar uma lista de compras vazia com nome definido.
-- RQ020: Cada lista pertence exclusivamente ao usuário que a criou (FK usuario_id NOT NULL).
-- RQ021: O usuário pode adicionar itens (variações de produto) a uma lista.
-- RQ022: Não é permitido adicionar o mesmo item duas vezes na mesma lista (UNIQUE lista_id + variacao_id).
-- RQ023: A exclusão de uma lista remove automaticamente todos os seus itens (ON DELETE CASCADE).

CREATE TABLE listas_compras (
    id           BIGSERIAL    PRIMARY KEY,
    usuario_id   BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nome         VARCHAR(100) NOT NULL,
    criado_em    TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_listas_compras_usuario ON listas_compras (usuario_id);

-- Itens dentro de cada lista: ligam lista ↔ variação do produto
CREATE TABLE itens_lista (
    id           BIGSERIAL PRIMARY KEY,
    lista_id     BIGINT    NOT NULL REFERENCES listas_compras(id) ON DELETE CASCADE,
    variacao_id  BIGINT    NOT NULL REFERENCES variacoes_produto(id) ON DELETE CASCADE,
    quantidade   INTEGER   NOT NULL DEFAULT 1,
    comprado     BOOLEAN   NOT NULL DEFAULT FALSE,
    UNIQUE (lista_id, variacao_id)
);

CREATE INDEX idx_itens_lista_lista ON itens_lista (lista_id);
