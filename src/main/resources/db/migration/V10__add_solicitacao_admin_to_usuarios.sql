-- Migration V10: Adicionando coluna solicitacao_adm aos usuarios
ALTER TABLE usuarios ADD COLUMN solicitacao_admin BOOLEAN DEFAULT FALSE NOT NULL;