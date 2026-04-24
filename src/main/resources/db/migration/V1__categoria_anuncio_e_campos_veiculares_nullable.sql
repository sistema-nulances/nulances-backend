DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'anuncios'
    ) THEN
        ALTER TABLE anuncios
            ADD COLUMN IF NOT EXISTS categoria VARCHAR(60);

        UPDATE anuncios
        SET categoria = 'COMERCIO'
        WHERE categoria IS NULL;

        ALTER TABLE anuncios
            ALTER COLUMN categoria SET NOT NULL,
            ALTER COLUMN marca_id DROP NOT NULL,
            ALTER COLUMN tipo DROP NOT NULL,
            ALTER COLUMN condicao DROP NOT NULL,
            ALTER COLUMN ano DROP NOT NULL,
            ALTER COLUMN combustivel DROP NOT NULL,
            ALTER COLUMN cambio DROP NOT NULL;
    END IF;
END $$;
