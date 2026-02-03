package com.example.vidaplena.config;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.entity.MedicalSpecialty;
import com.example.vidaplena.domain.enums.UserRole;
import com.example.vidaplena.repository.MedicalSpecialtyRepository;
import com.example.vidaplena.repository.UserRepository;
import com.example.vidaplena.service.AppointmentStatusService;
import com.example.vidaplena.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inicializador de dados do sistema.
 * 
 * <p>
 * Executa na inicialização da aplicação para:
 * </p>
 * <ul>
 * <li>Criar especialidades médicas padrão</li>
 * <li>Criar status padrão de atendimentos</li>
 * <li>Criar usuário administrador padrão (se não existir)</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final AppointmentStatusService statusService;
        private final UserService userService;
        private final UserRepository userRepository;
        private final MedicalSpecialtyRepository specialtyRepository;

        @Override
        public void run(String... args) {
                log.info("=== Iniciando configuração de dados do sistema ===");

                // Inicializar especialidades médicas
                initializeSpecialties();

                // Inicializar status de atendimentos
                statusService.initializeDefaultStatuses();

                // Criar usuário admin padrão se não existir
                createDefaultAdminUser();

                // Criar médicos padrão se não existirem
                createDefaultDoctors();

                // Criar recepcionistas padrão se não existirem
                createDefaultReceptionists();

                log.info("=== Configuração de dados concluída ===");
        }

        /**
         * Inicializa as especialidades médicas padrão do sistema.
         */
        private void initializeSpecialties() {
                if (specialtyRepository.count() == 0) {
                        log.info("Inicializando especialidades médicas...");

                        List<MedicalSpecialty> specialties = List.of(
                                        MedicalSpecialty.builder()
                                                        .code("GENERAL_PRACTICE")
                                                        .name("Clínica Geral")
                                                        .description("Atendimento médico geral para diagnóstico e tratamento de condições comuns")
                                                        .build(),
                                        MedicalSpecialty.builder()
                                                        .code("PEDIATRICS")
                                                        .name("Pediatria")
                                                        .description("Especialidade médica dedicada à saúde de crianças e adolescentes")
                                                        .build(),
                                        MedicalSpecialty.builder()
                                                        .code("CARDIOLOGY")
                                                        .name("Cardiologia")
                                                        .description(
                                                                        "Especialidade focada no diagnóstico e tratamento de doenças do coração e sistema cardiovascular")
                                                        .build(),
                                        MedicalSpecialty.builder()
                                                        .code("DERMATOLOGY")
                                                        .name("Dermatologia")
                                                        .description("Especialidade que trata de doenças e condições da pele, cabelo e unhas")
                                                        .build(),
                                        MedicalSpecialty.builder()
                                                        .code("ORTHOPEDICS")
                                                        .name("Ortopedia")
                                                        .description("Especialidade focada em problemas do sistema musculoesquelético")
                                                        .build());

                        specialtyRepository.saveAll(java.util.Objects.requireNonNull(specialties));
                        log.info("Especialidades médicas inicializadas com sucesso! Total: {}", specialties.size());
                } else {
                        log.info("Especialidades médicas já existem no sistema");
                }
        }

        /**
         * Cria um usuário administrador padrão se não existir nenhum admin.
         */
        private void createDefaultAdminUser() {
                // Verificar se já existe algum usuário admin
                boolean adminExists = userRepository.findByActiveTrue().stream()
                                .anyMatch(user -> user.getRole() == UserRole.ADMIN);

                if (!adminExists) {
                        log.info("Nenhum administrador encontrado. Criando usuário admin padrão...");

                        CreateUserRequest adminRequest = CreateUserRequest.builder()
                                        .name("Administrador")
                                        .email("admin@vidaplena.com")
                                        .password("admin123") // IMPORTANTE: Alterar em produção!
                                        .role(UserRole.ADMIN)
                                        .build();

                        userService.create(adminRequest);

                        log.warn("===================================================");
                        log.warn("USUÁRIO ADMIN PADRÃO CRIADO:");
                        log.warn("Email: admin@vidaplena.com");
                        log.warn("Senha: admin123");
                        log.warn("IMPORTANTE: Altere a senha em produção!");
                        log.warn("===================================================");
                } else {
                        log.info("Usuário administrador já existe no sistema");
                }
        }

        /**
         * Cria médicos padrão se não existirem.
         * 
         * <p>
         * Médicos criados:
         * </p>
         * <ul>
         * <li>Dr. João Silva - Clínica Geral</li>
         * <li>Dra. Maria Santos - Pediatria</li>
         * <li>Dr. Carlos Oliveira - Cardiologia</li>
         * </ul>
         */
        private void createDefaultDoctors() {
                // Verificar se já existem médicos
                boolean doctorsExist = userRepository.findByActiveTrue().stream()
                                .anyMatch(user -> user.getRole() == UserRole.DOCTOR);

                if (!doctorsExist) {
                        log.info("Nenhum médico encontrado. Criando médicos padrão...");

                        // Dr. João Silva - Clínica Geral
                        CreateUserRequest doctor1 = CreateUserRequest.builder()
                                        .name("Dr. João Silva")
                                        .email("joao.silva@vidaplena.com")
                                        .password("medico123")
                                        .role(UserRole.DOCTOR)
                                        .build();

                        // Dra. Maria Santos - Pediatria
                        CreateUserRequest doctor2 = CreateUserRequest.builder()
                                        .name("Dra. Maria Santos")
                                        .email("maria.santos@vidaplena.com")
                                        .password("medico123")
                                        .role(UserRole.DOCTOR)
                                        .build();

                        // Dr. Carlos Oliveira - Cardiologia
                        CreateUserRequest doctor3 = CreateUserRequest.builder()
                                        .name("Dr. Carlos Oliveira")
                                        .email("carlos.oliveira@vidaplena.com")
                                        .password("medico123")
                                        .role(UserRole.DOCTOR)
                                        .build();

                        userService.create(doctor1);
                        userService.create(doctor2);
                        userService.create(doctor3);

                        log.info("Médicos padrão criados com sucesso! Total: 3");
                        log.info("  - Dr. João Silva (joao.silva@vidaplena.com)");
                        log.info("  - Dra. Maria Santos (maria.santos@vidaplena.com)");
                        log.info("  - Dr. Carlos Oliveira (carlos.oliveira@vidaplena.com)");
                } else {
                        log.info("Médicos já existem no sistema");
                }
        }

        /**
         * Cria recepcionistas padrão se não existirem.
         * 
         * <p>
         * Recepcionistas criadas:
         * </p>
         * <ul>
         * <li>Ana Costa</li>
         * <li>Pedro Alves</li>
         * </ul>
         */
        private void createDefaultReceptionists() {
                // Verificar se já existem recepcionistas
                boolean receptionistsExist = userRepository.findByActiveTrue().stream()
                                .anyMatch(user -> user.getRole() == UserRole.RECEPTIONIST);

                if (!receptionistsExist) {
                        log.info("Nenhuma recepcionista encontrada. Criando recepcionistas padrão...");

                        // Ana Costa
                        CreateUserRequest receptionist1 = CreateUserRequest.builder()
                                        .name("Ana Costa")
                                        .email("ana.costa@vidaplena.com")
                                        .password("recepcao123")
                                        .role(UserRole.RECEPTIONIST)
                                        .build();

                        // Pedro Alves
                        CreateUserRequest receptionist2 = CreateUserRequest.builder()
                                        .name("Pedro Alves")
                                        .email("pedro.alves@vidaplena.com")
                                        .password("recepcao123")
                                        .role(UserRole.RECEPTIONIST)
                                        .build();

                        userService.create(receptionist1);
                        userService.create(receptionist2);

                        log.info("Recepcionistas padrão criadas com sucesso! Total: 2");
                        log.info("  - Ana Costa (ana.costa@vidaplena.com)");
                        log.info("  - Pedro Alves (pedro.alves@vidaplena.com)");
                } else {
                        log.info("Recepcionistas já existem no sistema");
                }
        }
}
