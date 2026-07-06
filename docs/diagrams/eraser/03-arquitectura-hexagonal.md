---
title: Arquitectura Hexagonal - Servicio de Auth
---

INPUT ADAPTERS:
  - "AuthController (REST)"
  - "JwtAuthFilter (Seguridad)"
  - "GlobalExceptionHandler"

DOMAIN CORE:
  - "AuthService (Caso de uso)"
  - "Modelos: User, OtpCode, RefreshToken"
  - "Puertos Entrada: AuthUseCase"
  - "Puertos Salida: UserRepository, OtpCodeRepository, RefreshTokenRepository, EmailSender, PasswordEncoder, JwtTokenService"

OUTPUT ADAPTERS:
  - "JpaUserRepositoryAdapter"
  - "JpaOtpCodeRepositoryAdapter"
  - "JpaRefreshTokenRepositoryAdapter"
  - "ConsoleEmailAdapter"
  - "BcryptPasswordEncoderAdapter"
  - "JwtTokenServiceAdapter"

JPA Entities:
  - "UserEntity (@Entity)"
  - "OtpCodeEntity (@Entity)"
  - "RefreshTokenEntity (@Entity)"

DB:
  - "PostgreSQL"

INPUT ADAPTERS -> DOMAIN CORE: "Llaman a puertos de entrada"
DOMAIN CORE -> OUTPUT ADAPTERS: "Usa puertos de salida"
OUTPUT ADAPTERS -> JPA Entities
JPA Entities -> DB

style DOMAIN CORE fill:#F59E0B,color:#000,stroke:#B45309
style INPUT ADAPTERS fill:#0C4A6E,color:#fff,stroke:#0EA5E9
style OUTPUT ADAPTERS fill:#064E3B,color:#fff,stroke:#10B981
