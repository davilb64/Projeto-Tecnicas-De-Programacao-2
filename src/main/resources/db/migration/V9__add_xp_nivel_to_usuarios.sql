-- Migration V9: Adicionando coluna xp e nivel aos usuarios
ALTER TABLE usuarios ADD COLUMN xp INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE usuarios ADD COLUMN nivel INTEGER DEFAULT 1 NOT NULL;