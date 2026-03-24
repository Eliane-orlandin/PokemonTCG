-- Roteiro de criação das tabelas no SQLite
-- Comentários: Esta tabela guardará o seu catálogo pessoal de cards do Pokémon TCG.

CREATE TABLE IF NOT EXISTS catalog (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    card_id       TEXT    NOT NULL,        -- ID único da API (ex: swsh1-1)
    card_name     TEXT    NOT NULL,        -- Nome do Card para exibição rápida
    series_id     TEXT    NOT NULL,        -- ID da Série (ex: base)
    series_name   TEXT    NOT NULL,        -- Nome da Série
    image_url     TEXT,                   -- Link da imagem do card
    quantity      INTEGER NOT NULL DEFAULT 1, -- Quantos cards você tem
    language      TEXT    NOT NULL DEFAULT 'pt', -- Idioma do card
    notes         TEXT,                   -- Suas anotações pessoais
    added_at      TEXT    NOT NULL,        -- Data em que foi adicionado (ISO-8601)
    updated_at    TEXT    NOT NULL         -- Data da última alteração
);

-- Índices ajudam a busca ficar muito mais rápida
CREATE INDEX IF NOT EXISTS idx_card_id   ON catalog(card_id);
CREATE INDEX IF NOT EXISTS idx_series_id ON catalog(series_id);
