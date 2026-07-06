---
title: Diagrama de Despliegue - CI/CD Tech Cup
---

DEV:
  - "VS Code / IntelliJ"
  - "Docker Desktop (PostgreSQL)"
  - "mvn clean test + JaCoCo"

GitHub Actions:
  - "CI Pipeline"
  - "1. mvn compile"
  - "2. mvn test (PostgreSQL service)"
  - "3. JaCoCo coverage > 80%"

Resultados:
  - "✅ Slack notificacion: Build exitoso"
  - "🚨 Slack notificacion: Build fallo"
  - "🚨 Crear Issue en GitHub automaticamente"

QA:
  - "Spring Boot"
  - "PostgreSQL"
  - "Pruebas de integracion"
  - "Validacion de calidad"

PROD:
  - "Spring Boot"
  - "PostgreSQL"
  - "Usuarios reales"
  - "Monitoreo continuo"

DEV -> GitHub Actions: "git push"
GitHub Actions -> QA: "Deploy si tests pasan"
QA -> PROD: "Deploy si validacion OK"
