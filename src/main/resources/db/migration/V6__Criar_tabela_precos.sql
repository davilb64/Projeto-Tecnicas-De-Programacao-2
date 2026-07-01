-- Migration V6: Tabela de histórico de preços
-- Base para o sistema colaborativo e gamificação do aplicativo

CREATE TABLE precos (
                        id                 BIGSERIAL      PRIMARY KEY,
                        variacao_id        BIGINT         NOT NULL REFERENCES variacoes_produto(id) ON DELETE CASCADE,
                        estabelecimento_id BIGINT         NOT NULL REFERENCES estabelecimentos(id)  ON DELETE CASCADE,
                        usuario_id         BIGINT         NOT NULL REFERENCES usuarios(id)          ON DELETE CASCADE,
                        valor              NUMERIC(10, 2) NOT NULL CHECK (valor > 0),
                        data_registro      TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Índices vitais para performance nas consultas de comparação de preços (Fase 2)
CREATE INDEX idx_precos_variacao ON precos (variacao_id);
CREATE INDEX idx_precos_estabelecimento ON precos (estabelecimento_id);
CREATE INDEX idx_precos_data_registro ON precos (data_registro);