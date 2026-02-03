-- Migration para adicionar colunas de soft delete e auditoria
-- Versão: 3
-- Descrição: Adiciona suporte para soft delete com Hibernate e auditoria automática

-- ============================================================================
-- USERS
-- ============================================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Criar índice para performance em queries com soft delete
CREATE INDEX IF NOT EXISTS idx_users_deleted ON users(deleted) WHERE deleted = false;

-- ============================================================================
-- APPOINTMENTS
-- ============================================================================
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

-- Appointments já tem created_at e updated_at, mas precisa de auditoria de usuário
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS created_by_user VARCHAR(100);
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS updated_by_user VARCHAR(100);

-- Remover coluna created_by antiga (FK para User) se existir
-- Comentado para evitar perda de dados - executar manualmente se necessário
-- ALTER TABLE appointments DROP COLUMN IF EXISTS created_by;

CREATE INDEX IF NOT EXISTS idx_appointments_deleted ON appointments(deleted) WHERE deleted = false;

-- ============================================================================
-- MEDICAL_SPECIALTIES
-- ============================================================================
ALTER TABLE medical_specialties ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE medical_specialties ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE medical_specialties ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_medical_specialties_deleted ON medical_specialties(deleted) WHERE deleted = false;

-- ============================================================================
-- APPOINTMENT_STATUS
-- ============================================================================
ALTER TABLE appointment_status ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE appointment_status ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE appointment_status ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_appointment_status_deleted ON appointment_status(deleted) WHERE deleted = false;

-- ============================================================================
-- COMENTÁRIOS E OBSERVAÇÕES
-- ============================================================================

-- Soft Delete:
-- - deleted = false: registro ativo (padrão)
-- - deleted = true: registro deletado logicamente
-- - deleted_at: timestamp de quando foi deletado
-- - deleted_by: email do usuário que deletou

-- Auditoria:
-- - created_by: email do usuário que criou (preenchido automaticamente)
-- - updated_by: email do usuário que atualizou (preenchido automaticamente)
-- - created_at: timestamp de criação (já existe)
-- - updated_at: timestamp de atualização (já existe)

-- Índices Parciais:
-- - Criados apenas para registros não deletados (deleted = false)
-- - Melhora performance de queries que buscam apenas registros ativos
-- - Reduz tamanho do índice

-- Compatibilidade:
-- - Campo 'active' mantido para compatibilidade com código existente
-- - Será removido em versões futuras após migração completa
