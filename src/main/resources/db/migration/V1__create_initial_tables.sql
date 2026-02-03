-- ============================================================================
-- VIDA PLENA - Migration V1: Criar Tabelas Iniciais
-- ============================================================================
-- Descrição: Cria todas as tabelas base do sistema
-- Autor: VIDA PLENA Team
-- Data: 2026-02-02
-- ============================================================================

-- ============================================================================
-- TABELA: users
-- Descrição: Armazena usuários do sistema (Admin, Médicos, Recepcionistas)
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'DOCTOR', 'RECEPTIONIST')),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

COMMENT ON TABLE users IS 'Usuários do sistema com diferentes roles';
COMMENT ON COLUMN users.role IS 'Papel do usuário: ADMIN, DOCTOR, RECEPTIONIST';

-- ============================================================================
-- TABELA: medical_specialties
-- Descrição: Especialidades médicas disponíveis na clínica
-- ============================================================================
CREATE TABLE IF NOT EXISTS medical_specialties (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    
    CONSTRAINT uk_specialty_code UNIQUE (code)
);

CREATE INDEX idx_specialties_code ON medical_specialties(code);
CREATE INDEX idx_specialties_active ON medical_specialties(active);

COMMENT ON TABLE medical_specialties IS 'Especialidades médicas: Clínica Geral, Pediatria, Cardiologia';

-- ============================================================================
-- TABELA: appointment_status
-- Descrição: Status possíveis de um atendimento
-- ============================================================================
CREATE TABLE IF NOT EXISTS appointment_status (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    
    CONSTRAINT uk_status_code UNIQUE (code)
);

CREATE INDEX idx_status_code ON appointment_status(code);
CREATE INDEX idx_status_active ON appointment_status(active);

COMMENT ON TABLE appointment_status IS 'Status de atendimento: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED';

-- ============================================================================
-- TABELA: appointments
-- Descrição: Atendimentos agendados
-- ============================================================================
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient VARCHAR(100) NOT NULL,
    doctor_id UUID NOT NULL,
    specialty_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    scheduled_date TIMESTAMP NOT NULL,
    notes TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES users(id),
    CONSTRAINT fk_appointment_specialty FOREIGN KEY (specialty_id) REFERENCES medical_specialties(id),
    CONSTRAINT fk_appointment_status FOREIGN KEY (status_id) REFERENCES appointment_status(id)
);

CREATE INDEX idx_appointment_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointment_specialty ON appointments(specialty_id);
CREATE INDEX idx_appointment_status ON appointments(status_id);
CREATE INDEX idx_appointment_scheduled_date ON appointments(scheduled_date);
CREATE INDEX idx_appointment_patient ON appointments(patient);

COMMENT ON TABLE appointments IS 'Atendimentos médicos agendados';
COMMENT ON COLUMN appointments.patient IS 'Nome do paciente (não há cadastro de pacientes)';

-- ============================================================================
-- TRIGGERS: Atualizar updated_at automaticamente
-- ============================================================================

-- Função para atualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para users
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger para appointments
CREATE TRIGGER update_appointments_updated_at
    BEFORE UPDATE ON appointments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMENTÁRIOS FINAIS
-- ============================================================================

COMMENT ON SCHEMA public IS 'Schema principal do VIDA PLENA';

-- ============================================================================
-- FIM DA MIGRATION V1
-- ============================================================================
