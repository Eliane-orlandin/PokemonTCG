-- Roteiro de criação das tabelas no SQLite
-- Comentários: Esta tabela guardará o seu catálogo pessoal de cards do Pokémon TCG.

CREATE TABLE IF NOT EXISTS catalog (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    card_id       TEXT    NOT NULL UNIQUE, -- ID único da API (ex: swsh1-1) — UNIQUE previne duplicatas
    card_name     TEXT    NOT NULL,        -- Nome do Card para exibição rápida
    series_id     TEXT    NOT NULL,        -- ID da Série (ex: base)
    series_name   TEXT    NOT NULL,        -- Nome da Série
    type          TEXT,                   -- Tipo do Pokémon (ex: Fire, Water)
    rarity        TEXT,                   -- Raridade (ex: Rare Holo)
    stage         TEXT    DEFAULT 'Básico',
    category      TEXT    DEFAULT 'Pokémon',
    image_url     TEXT,                   -- Link da imagem do card
    quantity      INTEGER NOT NULL DEFAULT 1, -- Quantos cards você tem
    language      TEXT    NOT NULL DEFAULT 'pt', -- Idioma do card
    notes         TEXT,                   -- Suas anotações pessoais
    added_at      TEXT    NOT NULL,        -- Data em que foi adicionado (ISO-8601)
    updated_at    TEXT    NOT NULL         -- Data da última alteração
);

-- Índice único como fallback para bancos existentes (CREATE TABLE IF NOT EXISTS não altera tabelas)
CREATE UNIQUE INDEX IF NOT EXISTS idx_card_id_unique ON catalog(card_id);

-- Índices ajudam a busca ficar muito mais rápida
CREATE INDEX IF NOT EXISTS idx_series_id ON catalog(series_id);
