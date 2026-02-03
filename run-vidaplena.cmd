@echo off
REM ============================================================================
REM VIDA PLENA - Script de Inicialização Automática (Batch Wrapper)
REM ============================================================================
REM Este script chama o script PowerShell principal
REM ============================================================================

echo.
echo ============================================================================
echo   VIDA PLENA - Inicializacao Automatica
echo ============================================================================
echo.

REM Executar o script PowerShell
powershell -ExecutionPolicy Bypass -File "%~dp0run-vidaplena.ps1"

REM Verificar se houve erro
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] Falha ao executar o script PowerShell
    echo.
    pause
    exit /b 1
)

pause
