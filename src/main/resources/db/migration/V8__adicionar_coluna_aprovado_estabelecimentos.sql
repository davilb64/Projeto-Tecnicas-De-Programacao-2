-- Migration V8: Adicionando coluna aprovado aos mercados
ALTER TABLE estabelecimentos
    ADD COLUMN aprovado BOOLEAN NOT NULL DEFAULT TRUE;