-- Roteiro de criação das tabelas no SQLite
CREATE TABLE IF NOT EXISTS catalog (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    card_id       TEXT    NOT NULL UNIQUE,
    card_name     TEXT    NOT NULL,
    series_id     TEXT    NOT NULL,
    series_name   TEXT    NOT NULL,
    type          TEXT,
    rarity        TEXT,
    stage         TEXT    DEFAULT 'Básico',
    category      TEXT    DEFAULT 'Pokémon',
    image_url     TEXT,
    quantity      INTEGER NOT NULL DEFAULT 1,
    language      TEXT    NOT NULL DEFAULT 'pt',
    notes         TEXT,
    added_at      TEXT    NOT NULL,
    updated_at    TEXT    NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_card_id_unique ON catalog(card_id);
CREATE INDEX IF NOT EXISTS idx_series_id ON catalog(series_id);
