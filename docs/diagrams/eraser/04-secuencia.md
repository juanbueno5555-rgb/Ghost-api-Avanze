---
title: Diagrama de Secuencia - Registro y Login
---

Usuario [icon: user]
API Gateway [color: #B45309]
Auth Service [color: #78350F]
BaseDeDatos [icon: database] [color: #3B0764]
JWT Token [color: #0C4A6E]

Usuario > API Gateway: "POST /register"
API Gateway > Auth Service: "Registrar usuario"
Auth Service > BaseDeDatos: "Guardar user (verified=false)"
BaseDeDatos > Auth Service: "OK"
Auth Service > Auth Service: "Generar OTP 6 digitos"
Auth Service > Usuario: "OTP enviado al email"

note: Usuario recibe OTP

Usuario > API Gateway: "POST /verify-otp"
API Gateway > Auth Service: "Validar OTP"
Auth Service > BaseDeDatos: "Verificar codigo"
BaseDeDatos > Auth Service: "Valido"
Auth Service > BaseDeDatos: "user.verified = true"
Auth Service > JWT Token: "Generar JWT"
JWT Token > Usuario: "JWT + Refresh Token"

note: Usuario autenticado. El JWT se valida en cada request.
