---
title: Diagrama de Contexto - Tech Cup
---

Usuario:
  - "Navegador Web / Mobile"

TechCup API Gateway:
  - Enrutamiento
  - Validacion JWT
  - Rate Limiting

Microservicios Auth:
  - Puerto: 8081
  - Autenticacion OTP + JWT

Microservicios Usuarios:
  - Puerto: 8082
  - Perfiles deportivos

Microservicios Equipos:
  - Puerto: 8083
  - Gestion de equipos

Microservicios Torneos:
  - Puerto: 8084
  - Ciclo de vida torneos

Microservicios Competencia:
  - Puerto: 8085
  - Partidos + Arbitraje

Microservicios Logistica:
  - Puerto: 8086
  - Refrigerios, dotacion

Microservicios Comunicaciones:
  - Puerto: 8087
  - Chat, soporte

Microservicios Notificaciones:
  - Puerto: 8088
  - Alertas

Microservicios Estadisticas:
  - Puerto: 8089
  - Rankings, paneles

Microservicios Auditoria:
  - Puerto: 8090
  - Trazabilidad

PostgreSQL:
  - "1 BD por microservicio"

EmailService:
  - "Envio de OTP"

Usuario -> TechCup API Gateway: "HTTP Requests"
TechCup API Gateway -> Microservicios Auth: "Registro / Login"
TechCup API Gateway -> Microservicios Usuarios: "Gestion perfiles"
TechCup API Gateway -> Microservicios Equipos: "CRUD equipos"
TechCup API Gateway -> Microservicios Torneos: "Crear torneo"
TechCup API Gateway -> Microservicios Competencia: "Partidos"
TechCup API Gateway -> Microservicios Logistica: "Entregas"
TechCup API Gateway -> Microservicios Comunicaciones: "Chats"
TechCup API Gateway -> Microservicios Notificaciones: "Alertas"
TechCup API Gateway -> Microservicios Estadisticas: "Reportes"
TechCup API Gateway -> Microservicios Auditoria: "Logs"
Microservicios Auth -> EmailService: "Enviar OTP"
Microservicios Auth -> PostgreSQL
Microservicios Usuarios -> PostgreSQL
Microservicios Equipos -> PostgreSQL
Microservicios Torneos -> PostgreSQL
Microservicios Competencia -> PostgreSQL
Microservicios Logistica -> PostgreSQL
Microservicios Comunicaciones -> PostgreSQL
Microservicios Notificaciones -> PostgreSQL
Microservicios Estadisticas -> PostgreSQL
Microservicios Auditoria -> PostgreSQL
