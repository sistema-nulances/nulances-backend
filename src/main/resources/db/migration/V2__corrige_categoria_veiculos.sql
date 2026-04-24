DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'anuncios'
    ) THEN
        UPDATE anuncios
        SET categoria = 'VEICULOS'
        WHERE categoria IS NULL
           OR (
               categoria = 'COMERCIO'
               AND (
                   marca_id IS NOT NULL
                   OR tipo IS NOT NULL
                   OR ano IS NOT NULL
                   OR combustivel IS NOT NULL
                   OR cambio IS NOT NULL
               )
           );
    END IF;
END $$;
