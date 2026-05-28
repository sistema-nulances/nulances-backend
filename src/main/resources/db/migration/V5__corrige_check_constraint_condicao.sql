-- Remove qualquer CHECK constraint legada do Hibernate na coluna condicao da tabela anuncios.
-- O Hibernate 6 gera automaticamente CHECKs para @Enumerated(EnumType.STRING). Quando os
-- valores do enum CondicaoAnuncioVeiculo mudaram de PEQUENA_MONTA/MEDIA_MONTA/GRANDE_MONTA
-- para NOVO/USADO/SEMINOVO, a constraint antiga bloqueava novos INSERTs, causando 500.
DO $$
DECLARE
    r RECORD;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'anuncios'
    ) THEN
        -- Remove todas as CHECK constraints que referenciam a coluna condicao
        FOR r IN
            SELECT con.conname
            FROM pg_constraint con
            INNER JOIN pg_class rel ON rel.oid = con.conrelid
            INNER JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
            WHERE con.contype = 'c'
              AND rel.relname = 'anuncios'
              AND nsp.nspname = 'public'
              AND pg_get_constraintdef(con.oid) ILIKE '%condicao%'
        LOOP
            EXECUTE 'ALTER TABLE anuncios DROP CONSTRAINT IF EXISTS ' || quote_ident(r.conname);
        END LOOP;

        -- Remove também possíveis constraints nas outras tabelas de veículos (defensivo)
        -- Restringe coluna cambio
        FOR r IN
            SELECT con.conname
            FROM pg_constraint con
            INNER JOIN pg_class rel ON rel.oid = con.conrelid
            INNER JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
            WHERE con.contype = 'c'
              AND rel.relname = 'anuncios'
              AND nsp.nspname = 'public'
              AND pg_get_constraintdef(con.oid) ILIKE '%cambio%'
        LOOP
            EXECUTE 'ALTER TABLE anuncios DROP CONSTRAINT IF EXISTS ' || quote_ident(r.conname);
        END LOOP;

        -- Restringe coluna combustivel
        FOR r IN
            SELECT con.conname
            FROM pg_constraint con
            INNER JOIN pg_class rel ON rel.oid = con.conrelid
            INNER JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
            WHERE con.contype = 'c'
              AND rel.relname = 'anuncios'
              AND nsp.nspname = 'public'
              AND pg_get_constraintdef(con.oid) ILIKE '%combustivel%'
        LOOP
            EXECUTE 'ALTER TABLE anuncios DROP CONSTRAINT IF EXISTS ' || quote_ident(r.conname);
        END LOOP;

        -- Restringe coluna tipo
        FOR r IN
            SELECT con.conname
            FROM pg_constraint con
            INNER JOIN pg_class rel ON rel.oid = con.conrelid
            INNER JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
            WHERE con.contype = 'c'
              AND rel.relname = 'anuncios'
              AND nsp.nspname = 'public'
              AND pg_get_constraintdef(con.oid) ILIKE '%tipo%'
        LOOP
            EXECUTE 'ALTER TABLE anuncios DROP CONSTRAINT IF EXISTS ' || quote_ident(r.conname);
        END LOOP;

        -- Restringe coluna categoria
        FOR r IN
            SELECT con.conname
            FROM pg_constraint con
            INNER JOIN pg_class rel ON rel.oid = con.conrelid
            INNER JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
            WHERE con.contype = 'c'
              AND rel.relname = 'anuncios'
              AND nsp.nspname = 'public'
              AND pg_get_constraintdef(con.oid) ILIKE '%categoria%'
        LOOP
            EXECUTE 'ALTER TABLE anuncios DROP CONSTRAINT IF EXISTS ' || quote_ident(r.conname);
        END LOOP;
    END IF;
END $$;
