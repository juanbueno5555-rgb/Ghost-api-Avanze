---
title: Diagrama de Contexto - Tech Cup
---

Usuario [icon: user]

TechCup [color: #F59E0B] {
  API Gateway [icon: lock]
  Auth Service
  Usuarios Service
  Equipos Service
  Torneos Service
  Competencia Service
  Logistica Service
  Comunicaciones Service
  Notificaciones Service
  Estadisticas Service
  Auditoria Service
}

PostgreSQL [icon: database]
Email [icon: mail]

Usuario > API Gateway: "HTTP Requests"
API Gateway > Auth Service: "Registro/Login"
API Gateway > Usuarios Service
API Gateway > Equipos Service
API Gateway > Torneos Service
API Gateway > Competencia Service
API Gateway > Logistica Service
API Gateway > Comunicaciones Service
API Gateway > Notificaciones Service
API Gateway > Estadisticas Service
API Gateway > Auditoria Service
Auth Service > Email: "Envio OTP"
Auth Service > PostgreSQL
Usuarios Service > PostgreSQL
Equipos Service > PostgreSQL
Torneos Service > PostgreSQL
Competencia Service > PostgreSQL
Logistica Service > PostgreSQL
Comunicaciones Service > PostgreSQL
Notificaciones Service > PostgreSQL
Estadisticas Service > PostgreSQL
Auditoria Service > PostgreSQL
