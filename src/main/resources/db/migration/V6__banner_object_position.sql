-- Adiciona coluna object_position para controle do ponto focal do banner.
-- Formato: "X% Y%" (ex.: "50% 30%"). NULL = centro padrão (50% 50%).
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'banners'
    ) THEN
        ALTER TABLE banners
            ADD COLUMN IF NOT EXISTS object_position VARCHAR(20);
    END IF;
END $$;
