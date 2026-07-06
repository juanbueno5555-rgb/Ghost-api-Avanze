---
title: Arquitectura Hexagonal - Servicio de Auth
---

InputAdapters [color: #0C4A6E] {
  AuthController
  JwtAuthFilter
  GlobalExceptionHandler
}

DomainCore [color: #B45309] {
  AuthService [color: #F59E0B]
  User (POJO)
  OtpCode (POJO)
  RefreshToken (POJO)
  UserRepository (interface)
  AuthUseCase (interface)
}

OutputAdapters [color: #064E3B] {
  JpaUserRepositoryAdapter [color: #10B981]
  JpaOtpCodeAdapter [color: #10B981]
  ConsoleEmailAdapter [color: #10B981]
  BcryptEncoderAdapter [color: #10B981]
  JwtTokenServiceAdapter [color: #10B981]
}

PostgreSQL [icon: database] [color: #3B0764]

InputAdapters > DomainCore: "Llaman a puertos de entrada"
DomainCore > OutputAdapters: "Usa puertos de salida"
OutputAdapters > PostgreSQL
