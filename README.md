# 🏥 Clínica Vida Plena - Backend API

Sistema de gestão de atendimentos médicos desenvolvido com **Java 21**, **Spring Boot 3.4.2** e **PostgreSQL**. Focado em alta performance, segurança via JWT e arquitetura escalável.

---

## 🚀 Tecnologias
- **Stack**: Java 21, Spring Boot 3, Spring Security (JWT).
- **Banco**: PostgreSQL 16.
- **Mensageria**: Apache Kafka (Auditoria de eventos).
- **Documentação**: Swagger/OpenAPI.
- **Infra**: Docker & Docker Compose.

---

## 📦 Como Executar

### ⚡ Via Script de Automação (Recomendado)
O script verifica dependências, sobe os containers, aguarda a API ficar pronta e executa os testes:
- **Windows**: `.\run-vidaplena.cmd` ou PowerShell `.\run-vidaplena.ps1`

### 🐳 Via Docker Manual
```bash
docker-compose up -d --build
```
Acesse em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔐 Acesso e Autenticação
O sistema utiliza **JWT**. Para testar os endpoints protegidos, faça login e use o token no botão "Authorize" do Swagger.

### Credenciais de Teste
| Perfil | Email | Senha |
|---|---|---|
| **ADMIN** | `admin@vidaplena.com` | `admin123` |
| **DOCTOR** | `joao.silva@vidaplena.com` | `medico123` |
| **RECEP** | `ana.costa@vidaplena.com` | `recepcao123` |
---

## 👥 Autor
**Artur Henrique**  
[LinkedIn](https://www.linkedin.com/in/artur-henrique-carvalho/)

---
**Desenvolvido para a Clínica Vida Plena**
