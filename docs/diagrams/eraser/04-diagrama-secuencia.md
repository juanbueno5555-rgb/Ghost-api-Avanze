---
title: Diagrama de Secuencia - Registro y Login
---

Usuario -> API Gateway: POST /register (email, pass, datos)
API Gateway -> AuthService: Reenviar solicitud
AuthService -> DB: Guardar usuario (verified=false)
DB -> AuthService: OK
AuthService -> AuthService: Generar OTP (6 digitos)
AuthService -> EmailService: Enviar OTP
EmailService -> Usuario: 📧 Codigo OTP

note over Usuario: El usuario revisa su correo

Usuario -> API Gateway: POST /verify-otp (token, codigo)
API Gateway -> AuthService: Validar OTP
AuthService -> DB: Buscar OTP + marcar usado
DB -> AuthService: Valido
AuthService -> DB: Actualizar user.verified=true
DB -> AuthService: OK
AuthService -> AuthService: Generar JWT (access 15min + refresh 7d)
AuthService -> Usuario: 🎫 JWT + Refresh Token

note over Usuario: Usuario autenticado

Usuario -> API Gateway: GET /api/auth/me (Header: JWT)
API Gateway -> API Gateway: Validar JWT (firma + expiracion)
API Gateway -> AuthService: Obtener datos
AuthService -> Usuario: Datos del usuario
