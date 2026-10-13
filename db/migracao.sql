-- ===========================================================================
--  Estacionamento — migração do banco (Supabase / PostgreSQL)
--  Rode UMA vez no SQL Editor do Supabase.
--
--  Contexto: a tabela `pagamento` foi criada na Alteração 3 e acabou com
--  linhas de teste órfãs (auto_id sem veículo correspondente), feitas pelo
--  antigo PagamentoController (removido). Isso quebrava a migração automática
--  do Hibernate no boot. O front-end nunca gravou pagamento "de verdade"
--  nessa tabela, então limpá-la não perde nada real.
-- ===========================================================================

BEGIN;

-- 1) Garante a tabela (caso não exista em algum ambiente).
CREATE TABLE IF NOT EXISTS auto_model (
    id       VARCHAR(255) PRIMARY KEY,
    placa    VARCHAR(255) NOT NULL,
    tipo     VARCHAR(255) NOT NULL,
    valor    DOUBLE PRECISION NOT NULL,
    pago     BOOLEAN NOT NULL DEFAULT FALSE,
    entrada  BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS pagamento (
    id                VARCHAR(255) PRIMARY KEY,
    auto_id           VARCHAR(255) NOT NULL,
    metodo_pagamento  VARCHAR(255) NOT NULL,
    valor             DOUBLE PRECISION NOT NULL,
    troco             DOUBLE PRECISION,
    data              BIGINT
);

-- 2) Limpa lixo de teste e adiciona a coluna nova.
DELETE FROM pagamento
 WHERE auto_id NOT IN (SELECT id FROM auto_model);

ALTER TABLE pagamento ADD COLUMN IF NOT EXISTS troco DOUBLE PRECISION;
UPDATE pagamento SET troco = 0 WHERE troco IS NULL;

-- 3) Índice para carregar os pagamentos de um veículo rápido.
CREATE INDEX IF NOT EXISTS idx_pagamento_auto_id ON pagamento(auto_id);

-- 4) Blacklist: veículos que saíram sem pagar (com foto opcional em base64).
--    Precisa desta tabela porque em produção o Hibernate está com
--    ddl-auto=none (não cria tabela nova sozinho).
CREATE TABLE IF NOT EXISTS blacklist (
    id        VARCHAR(255) PRIMARY KEY,
    placa     VARCHAR(255) NOT NULL,
    motivo    VARCHAR(500),
    foto      TEXT,
    criado_em BIGINT NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_blacklist_criado_em ON blacklist(criado_em DESC);

-- 4) (Opcional) Se você usa RLS no Supabase e o backend conecta como service
--    role, pode ignorar. Caso precise de acesso público:
-- ALTER TABLE auto_model ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE pagamento  ENABLE ROW LEVEL SECURITY;
-- CREATE POLICY "public auto_model" ON auto_model FOR ALL USING (true) WITH CHECK (true);
-- CREATE POLICY "public pagamento"  ON pagamento  FOR ALL USING (true) WITH CHECK (true);

-- 5) Integridade (migração v2) ------------------------------------------------
--    a) Placa única sem diferenciar maiúscula/minúscula (o check do backend não
--       é atômico; este índice é a garantia real). Se falhar com "duplicate key",
--       há placas repetidas: veja com
--         SELECT lower(placa), count(*) FROM auto_model GROUP BY 1 HAVING count(*) > 1;
CREATE UNIQUE INDEX IF NOT EXISTS ux_auto_model_placa_lower ON auto_model (lower(placa));

--    b) pagamento.data passa a ser obrigatório (a entidade já exigia).
UPDATE pagamento SET data = (extract(epoch FROM now()) * 1000)::bigint WHERE data IS NULL;
ALTER TABLE pagamento ALTER COLUMN data SET NOT NULL;

--    c) FK com cascade: apagar o veículo apaga seus pagamentos e impede órfãos.
DELETE FROM pagamento WHERE auto_id NOT IN (SELECT id FROM auto_model);
ALTER TABLE pagamento DROP CONSTRAINT IF EXISTS fk_pagamento_auto;
ALTER TABLE pagamento
    ADD CONSTRAINT fk_pagamento_auto FOREIGN KEY (auto_id)
    REFERENCES auto_model(id) ON DELETE CASCADE;

COMMIT;
