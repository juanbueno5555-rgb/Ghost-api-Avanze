---
title: Diagrama de Componentes - Tech Cup
---

Usuario [icon: user]

API Gateway [color: #0F172A]

D1 Identidad [color: #1E1035] {
  Auth Service [color: #10B981]
  Usuarios Service [color: #F59E0B]
  Equipos Service [color: #F59E0B]
}

D2 Torneo [color: #1E1205] {
  Torneos Service [color: #F59E0B]
  Competencia Service [color: #F59E0B]
}

D3 Operaciones [color: #052E16] {
  Logistica Service [color: #10B981]
  Comunicaciones Service [color: #10B981]
  Notificaciones Service [color: #10B981]
}

D4 Plataforma [color: #1A032E] {
  Estadisticas Service [color: #C084FC]
  Auditoria Service [color: #C084FC]
}

EventBus [color: #082F49]
BaseDeDatos [icon: database] [color: #3B0764]

Usuario > API Gateway
API Gateway > D1 Identidad
API Gateway > D2 Torneo
API Gateway > D3 Operaciones
API Gateway > D4 Plataforma
D2 Torneo > EventBus: "Publica eventos de partido"
EventBus > D3 Operaciones: "Notificaciones push"
EventBus > D4 Plataforma: "Stats + Audit"
D1 Identidad > BaseDeDatos
D2 Torneo > BaseDeDatos
D3 Operaciones > BaseDeDatos
D4 Plataforma > BaseDeDatos
