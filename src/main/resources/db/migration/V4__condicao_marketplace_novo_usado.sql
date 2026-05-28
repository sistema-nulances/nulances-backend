-- Marketplace usa Novo/Usado/Seminovo; valores de leilão (monta) não se aplicam.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'anuncios'
    ) THEN
        UPDATE anuncios
        SET condicao = 'USADO'
        WHERE condicao IN ('PEQUENA_MONTA', 'MEDIA_MONTA', 'GRANDE_MONTA');
    END IF;
END $$;
