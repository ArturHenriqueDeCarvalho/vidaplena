#!/usr/bin/env pwsh
# ============================================================================
# VIDA PLENA - Script de Inicialização Automática
# ============================================================================
# Este script automatiza todo o processo de inicialização da aplicação:
# 1. Verifica pré-requisitos (Docker)
# 2. Para containers antigos (se existirem)
# 3. Reconstrói e inicia os containers Docker
# 4. Aguarda a aplicação ficar pronta
# 5. Executa os testes unitários
# 6. Exibe informações de acesso
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  VIDA PLENA - Inicialização Automática" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# Função para exibir mensagens coloridas
function Write-Step {
    param([string]$Message)
    Write-Host "[STEP] $Message" -ForegroundColor Yellow
}

function Write-Success {
    param([string]$Message)
    Write-Host "[OK] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERRO] $Message" -ForegroundColor Red
}

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

# ============================================================================
# PASSO 1: Verificar Pré-requisitos
# ============================================================================
Write-Step "Verificando pré-requisitos..."

# Verificar Docker
try {
    $dockerVersion = docker --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Docker instalado: $dockerVersion"
    }
    else {
        throw "Docker não encontrado"
    }
}
catch {
    Write-Error "Docker não está instalado ou não está no PATH"
    Write-Info "Instale o Docker Desktop: https://www.docker.com/products/docker-desktop"
    exit 1
}

# Verificar Docker Compose
try {
    $composeVersion = docker-compose --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Docker Compose instalado: $composeVersion"
    }
    else {
        throw "Docker Compose não encontrado"
    }
}
catch {
    Write-Error "Docker Compose não está instalado"
    exit 1
}

Write-Host ""

# ============================================================================
# PASSO 2: Parar Containers Antigos
# ============================================================================
Write-Step "Parando containers antigos (se existirem)..."

docker-compose down 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Success "Containers antigos removidos"
}
else {
    Write-Info "Nenhum container antigo encontrado"
}

Write-Host ""

# ============================================================================
# PASSO 3: Construir e Iniciar Containers
# ============================================================================
Write-Step "Construindo e iniciando containers Docker..."
Write-Info "Isso pode levar alguns minutos na primeira execução..."

docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao iniciar containers Docker"
    exit 1
}

Write-Success "Containers iniciados com sucesso"
Write-Host ""

# ============================================================================
# PASSO 4: Aguardar Aplicação Ficar Pronta
# ============================================================================
Write-Step "Aguardando aplicação ficar pronta..."

$maxAttempts = 60
$attempt = 0
$ready = $false

while ($attempt -lt $maxAttempts -and -not $ready) {
    $attempt++
    Write-Host "  Tentativa $attempt/$maxAttempts..." -NoNewline
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -Method Head -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $ready = $true
            Write-Host " OK" -ForegroundColor Green
        }
        else {
            Write-Host " Aguardando..." -ForegroundColor Yellow
            Start-Sleep -Seconds 2
        }
    }
    catch {
        Write-Host " Aguardando..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
}

if (-not $ready) {
    Write-Error "Aplicação não ficou pronta após $maxAttempts tentativas"
    Write-Info "Verifique os logs com: docker-compose logs app"
    exit 1
}

Write-Success "Aplicação está pronta e respondendo!"
Write-Host ""

# ============================================================================
# PASSO 5: Executar Testes Unitários
# ============================================================================
Write-Step "Executando testes unitários..."

.\mvnw.cmd clean test -q

if ($LASTEXITCODE -eq 0) {
    Write-Success "Todos os testes passaram com sucesso!"
}
else {
    Write-Error "Alguns testes falharam"
    Write-Info "Verifique os logs de teste para mais detalhes"
    Write-Info "A aplicação está rodando, mas pode haver problemas"
}

Write-Host ""

# ============================================================================
# PASSO 6: Exibir Informações de Acesso
# ============================================================================
Write-Host "============================================================================" -ForegroundColor Green
Write-Host "  APLICACAO VIDA PLENA RODANDO COM SUCESSO!" -ForegroundColor Green
Write-Host "============================================================================" -ForegroundColor Green
Write-Host ""

Write-Host "SWAGGER UI (Testar API):" -ForegroundColor Cyan
Write-Host "   http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host ""

Write-Host "CREDENCIAIS DE LOGIN:" -ForegroundColor Cyan
Write-Host "   Email:  admin@vidaplena.com" -ForegroundColor White
Write-Host "   Senha:  admin123" -ForegroundColor White
Write-Host ""

Write-Host "CONTAINERS DOCKER:" -ForegroundColor Cyan
docker-compose ps
Write-Host ""

Write-Host "COMANDOS UTEIS:" -ForegroundColor Cyan
Write-Host "   Ver logs:           docker-compose logs -f app" -ForegroundColor White
Write-Host "   Parar aplicacao:    docker-compose down" -ForegroundColor White
Write-Host "   Reiniciar:          docker-compose restart" -ForegroundColor White
Write-Host "   Executar testes:    .\mvnw.cmd test" -ForegroundColor White
Write-Host ""

Write-Host "DOCUMENTACAO:" -ForegroundColor Cyan
Write-Host "   Guia de Testes:     guia_testes_swagger.md (na pasta brain)" -ForegroundColor White
Write-Host "   Guia de Execucao:   guia_execucao.md (na pasta brain)" -ForegroundColor White
Write-Host "   README:             README.md" -ForegroundColor White
Write-Host ""

Write-Host "============================================================================" -ForegroundColor Green
Write-Host "  Boa sorte com os testes!" -ForegroundColor Green
Write-Host "============================================================================" -ForegroundColor Green
Write-Host ""
