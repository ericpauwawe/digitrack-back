CREATE TABLE IF NOT EXISTS public.material_labels (
    id SERIAL PRIMARY KEY,
    category1 VARCHAR(255) NOT NULL,
    category2 VARCHAR(255),
    label VARCHAR(255) NOT NULL,
    min_pct1 INTEGER NOT NULL,
    max_pct1 INTEGER NOT NULL,
    min_pct2 INTEGER NOT NULL,
    max_pct2 INTEGER NOT NULL
    );