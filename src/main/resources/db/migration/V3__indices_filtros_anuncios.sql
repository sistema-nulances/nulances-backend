-- Índices para acelerar a listagem pública com filtros combinados
-- (status + categoria/tipo/condicao/combustivel/cambio + created_at).
-- Idempotente: só cria se a tabela existir.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'anuncios'
    ) THEN
        CREATE INDEX IF NOT EXISTS idx_anuncios_status_categoria
            ON anuncios (status, categoria);

        CREATE INDEX IF NOT EXISTS idx_anuncios_status_tipo
            ON anuncios (status, tipo);

        CREATE INDEX IF NOT EXISTS idx_anuncios_status_condicao
            ON anuncios (status, condicao);

        CREATE INDEX IF NOT EXISTS idx_anuncios_status_combustivel
            ON anuncios (status, combustivel);

        CREATE INDEX IF NOT EXISTS idx_anuncios_status_cambio
            ON anuncios (status, cambio);

        CREATE INDEX IF NOT EXISTS idx_anuncios_status_created_at
            ON anuncios (status, created_at DESC);
    END IF;
END $$;
