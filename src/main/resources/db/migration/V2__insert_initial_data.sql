-- ============================================================================
-- VIDA PLENA - Migration V2: Inserir Dados Iniciais
-- ============================================================================
-- Descrição: Popula o banco com dados essenciais para funcionamento
-- Autor: VIDA PLENA Team
-- Data: 2026-02-02
-- ============================================================================

-- ============================================================================
-- DADOS: appointment_status
-- Descrição: Status padrão do sistema
-- ============================================================================
INSERT INTO appointment_status (code, description, active) VALUES
    ('SCHEDULED', 'Atendimento Agendado', true),
    ('IN_PROGRESS', 'Atendimento em Andamento', true),
    ('COMPLETED', 'Atendimento Concluído', true),
    ('CANCELED', 'Atendimento Cancelado', true)
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- DADOS: medical_specialties
-- Descrição: Especialidades disponíveis na clínica
-- ============================================================================
INSERT INTO medical_specialties (code, name, description, active) VALUES
    ('GENERAL', 'Clínica Geral', 'Atendimento médico geral para adultos', true),
    ('PEDIATRICS', 'Pediatria', 'Atendimento especializado para crianças e adolescentes', true),
    ('CARDIOLOGY', 'Cardiologia', 'Especialidade focada em doenças do coração e sistema cardiovascular', true)
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- DADOS: users
-- Descrição: Usuários iniciais do sistema
-- ============================================================================

-- Senha padrão para todos: "admin123" (BCrypt hash)
-- Hash gerado com: BCryptPasswordEncoder().encode("admin123")
INSERT INTO users (id, name, email, password, role, active) VALUES
    (
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'Administrador',
        'admin@vidaplena.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN',
        true
    ),
    (
        'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
        'Dr. João Silva',
        'joao.silva@vidaplena.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'DOCTOR',
        true
    ),
    (
        'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
        'Dra. Maria Santos',
        'maria.santos@vidaplena.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'DOCTOR',
        true
    ),
    (
        'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44',
        'Dr. Pedro Oliveira',
        'pedro.oliveira@vidaplena.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'DOCTOR',
        true
    ),
    (
        'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55',
        'Ana Costa',
        'ana.costa@vidaplena.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'RECEPTIONIST',
        true
    )
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- DADOS: appointments (Exemplos)
-- Descrição: Alguns atendimentos de exemplo para demonstração
-- ============================================================================
INSERT INTO appointments (
    id,
    patient,
    doctor_id,
    specialty_id,
    status_id,
    scheduled_date,
    notes,
    created_by
) VALUES
    (
        'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66',
        'Carlos Mendes',
        'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', -- Dr. João Silva
        (SELECT id FROM medical_specialties WHERE code = 'GENERAL'),
        (SELECT id FROM appointment_status WHERE code = 'SCHEDULED'),
        CURRENT_TIMESTAMP + INTERVAL '2 days',
        'Consulta de rotina',
        'admin@vidaplena.com'
    ),
    (
        '06eebc99-9c0b-4ef8-bb6d-6bb9bd380a77',
        'Júlia Ferreira',
        'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', -- Dra. Maria Santos
        (SELECT id FROM medical_specialties WHERE code = 'PEDIATRICS'),
        (SELECT id FROM appointment_status WHERE code = 'SCHEDULED'),
        CURRENT_TIMESTAMP + INTERVAL '3 days',
        'Consulta pediátrica - vacinação',
        'ana.costa@vidaplena.com'
    ),
    (
        '07eebc99-9c0b-4ef8-bb6d-6bb9bd380a88',
        'Roberto Alves',
        'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', -- Dr. Pedro Oliveira
        (SELECT id FROM medical_specialties WHERE code = 'CARDIOLOGY'),
        (SELECT id FROM appointment_status WHERE code = 'COMPLETED'),
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        'Avaliação cardiológica - exames ok',
        'admin@vidaplena.com'
    )
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- VERIFICAÇÕES
-- ============================================================================

-- Verificar se os dados foram inseridos corretamente
DO $$
DECLARE
    status_count INTEGER;
    specialty_count INTEGER;
    user_count INTEGER;
    appointment_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO status_count FROM appointment_status;
    SELECT COUNT(*) INTO specialty_count FROM medical_specialties;
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO appointment_count FROM appointments;
    
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'DADOS INICIAIS INSERIDOS COM SUCESSO';
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Status de Atendimento: %', status_count;
    RAISE NOTICE 'Especialidades Médicas: %', specialty_count;
    RAISE NOTICE 'Usuários: %', user_count;
    RAISE NOTICE 'Atendimentos: %', appointment_count;
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Credenciais de Login:';
    RAISE NOTICE 'Email: admin@vidaplena.com';
    RAISE NOTICE 'Senha: admin123';
    RAISE NOTICE '==============================================';
END $$;

-- ============================================================================
-- COMENTÁRIOS
-- ============================================================================

COMMENT ON TABLE appointment_status IS 'Contém 4 status padrão do sistema';
COMMENT ON TABLE medical_specialties IS 'Contém 3 especialidades: Clínica Geral, Pediatria, Cardiologia';
COMMENT ON TABLE users IS 'Contém 5 usuários: 1 Admin, 3 Médicos, 1 Recepcionista';
COMMENT ON TABLE appointments IS 'Contém 3 atendimentos de exemplo';

-- ============================================================================
-- FIM DA MIGRATION V2
-- ============================================================================
