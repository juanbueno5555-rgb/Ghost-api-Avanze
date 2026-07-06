---
title: Diagrama de Componentes - Tech Cup
---

API Gateway:
  - Enrutamiento
  - Validacion JWT
  - Rate Limiting
  - Logging

D1 Identidad y Personas:
  - "Servicio de Identidad (Auth)"
  - "Servicio de Usuarios y Jugadores"
  - "Servicio de Equipos"
  - "BD PostgreSQL x3"

D2 Torneo y Competencia:
  - "Servicio de Torneos"
  - "Servicio de Competencia (Partidos + Arbitraje)"
  - "BD PostgreSQL x2"

D3 Operaciones y Comunicacion:
  - "Servicio de Logistica"
  - "Servicio de Comunicaciones (Chat)"
  - "Servicio de Notificaciones"
  - "BD PostgreSQL x3"

D4 Plataforma e Inteligencia:
  - "Servicio de Estadisticas"
  - "Servicio de Auditoria"
  - "BD PostgreSQL x2"

EventBus:
  - "RabbitMQ / Kafka"
  - "Eventos: goles, tarjetas, sanciones, notificaciones"

Usuario -> API Gateway: "HTTP Requests"
API Gateway -> D1 Identidad y Personas
API Gateway -> D2 Torneo y Competencia
API Gateway -> D3 Operaciones y Comunicacion
API Gateway -> D4 Plataforma e Inteligencia

D2 Torneo y Competencia -> EventBus: "Publica eventos de partido"
EventBus -> D3 Operaciones y Comunicacion: "Dispara notificaciones"
EventBus -> D4 Plataforma e Inteligencia: "Stats + Auditoria consumen"
