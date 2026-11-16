@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
  echo Instale o Java JDK 17 ou superior e abra este arquivo novamente.
  echo As instrucoes completas estao no README.md.
  pause
  exit /b 1
)
if exist "territorio-alerta.jar" (
  java -jar territorio-alerta.jar
) else (
  where mvn >nul 2>nul
  if errorlevel 1 (
    echo Instale o Maven 3.9 ou superior para compilar a copia do GitHub.
    echo Depois execute mvn clean package conforme o README.md.
    pause
    exit /b 1
  )
  call mvn clean package
  if errorlevel 1 (
    echo A compilacao nao foi concluida. Confira as mensagens acima.
    pause
    exit /b 1
  )
  java -jar target\territorio-alerta-1.0.0.jar
)
pause
