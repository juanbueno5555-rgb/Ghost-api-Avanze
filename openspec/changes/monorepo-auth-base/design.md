# Design: Monorepo Base + Auth Microservice

## Technical Approach

Maven multi-module monorepo with a shared `common` library and independent Spring Boot microservices. Each service is a standalone deployable unit with its own database schema. The parent POM controls versions; services know nothing about each other at compile time.

## Architecture Decisions

| Decision | Choice | Alternatives | Rationale |
|----------|--------|-------------|-----------|
| Build tool | Maven multi-module | Gradle monorepo, single repo per service | Standard en universidad, IDEs lo soportan nativamente, más simple para el equipo |
| JWT library | `jjwt` (io.jsonwebtoken) | `nimbus-jose-jwt`, Spring Security OAuth2 | API más simple, ampliamente usado, mantiene activo |
| OTP storage | DB table with expiry + max attempts | Redis, in-memory cache | Sin Redis en el stack aún, DB alcanza para MVP. Fácil migrar a Redis después |
| Password hashing | BCrypt via Spring Security | Argon2, scrypt | Spring Security lo trae built-in, seguridad suficiente |
| Auth arch | Stateless JWT filter chain | Session-based, OAuth2 | Escalable, sin estado en servidor, fácil de integrar con API Gateway después |
| Common lib | Plain Maven module (no Spring Boot) | Spring Boot starter | Evita cargar contexto innecesario. Solo DTOs, utils, excepciones |
| DB per service | Shared DB, single schema | Schema-per-service, DB-per-service | DB-per-service es la meta, pero arrancamos con schema separado para simplificar dev. Cada servicio tiene su propio datasource |

## Data Flow

### Registration Flow
```
Client → POST /api/auth/register
  → AuthController
    → AuthService.register()
      → Validates input
      → Checks duplicate email
      → Hashes password (BCrypt)
      → Saves User (verified=false)
      → Generates OTP code
      → Stores OtpCode in DB
      → Sends OTP via EmailService (console mock)
      → Returns otpToken (reference to OtpCode ID)
```

### OTP Verification Flow
```
Client → POST /api/auth/verify-otp
  → AuthController
    → AuthService.verifyOtp()
      → Finds OtpCode by token
      → Validates not expired, not used, attempts < 5
      → Verifies OTP hash match
      → Marks User as verified=true
      → Marks OTP as used=true
      → Generates accessToken (15min) + refreshToken (7d)
      → Returns tokens + user info
```

### Login Flow
```
Client → POST /api/auth/login
  → AuthController
    → AuthService.login()
      → Finds User by email
      → Verifies BCrypt password
      → Checks verified=true
      → If OTP not verified → return 403
      → Generates tokens
      → Returns tokens + user info
```

### Token Validation (every authenticated request)
```
Client → Authorization: Bearer <accessToken>
  → JwtAuthenticationFilter
    → Extracts JWT from header
    → Validates signature + expiry
    → Loads user roles from token claims
    → Sets SecurityContext
  → Controller (if authorized)
```

## Package Structure

```
auth-service/
└── src/main/java/com/techcup/auth/
    ├── AuthApplication.java
    ├── config/
    │   ├── SecurityConfig.java        — Spring Security + filter chain
    │   ├── JwtConfig.java             — JWT secret, expiration properties
    │   └── OpenApiConfig.java         — Swagger/OpenAPI config
    ├── controller/
    │   ├── AuthController.java        — register, verifyOtp, login, refresh, etc.
    │   └── AdminController.java       — user management (admin)
    ├── dto/
    │   ├── request/                   — RegisterRequest, LoginRequest, etc.
    │   └── response/                  — AuthResponse, UserResponse, etc.
    ├── model/
    │   ├── User.java
    │   ├── OtpCode.java
    │   ├── RefreshToken.java
    │   └── Role.java                  — Enum
    ├── repository/
    │   ├── UserRepository.java
    │   ├── OtpCodeRepository.java
    │   └── RefreshTokenRepository.java
    ├── security/
    │   ├── JwtProvider.java           — Generate + validate JWT
    │   ├── JwtAuthenticationFilter.java — OncePerRequestFilter
    │   └── CustomUserDetailsService.java
    ├── service/
    │   ├── AuthService.java
    │   ├── OtpService.java
    │   ├── TokenService.java
    │   └── EmailService.java          — Interface (mock impl for now)
    └── exception/
        ├── GlobalExceptionHandler.java — @ControllerAdvice
        └── ApiException.java
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `pom.xml` | Create | Parent POM, Spring Boot 3.2.3, modules: common, auth-service |
| `common/pom.xml` | Create | Shared library (no Spring Boot plugin) |
| `common/src/main/java/com/techcup/common/...` | Create | Shared DTOs, exceptions, JWT utils |
| `auth-service/pom.xml` | Create | Spring Boot starter web, security, data-jpa, validation, jjwt, lombok |
| `auth-service/src/main/java/com/techcup/auth/**` | Create | Full auth service (see package structure) |
| `auth-service/src/main/resources/application.yml` | Create | Config: datasource, jwt, server port |
| `auth-service/src/test/java/com/techcup/auth/**` | Create | Unit + integration tests |
| `docker-compose.yml` | Create | PostgreSQL service for auth |
| `.gitignore` | Create | Java, Maven, IDE, OS ignores |
| `.github/workflows/ci.yml` | Create | Maven build + test + JaCoCo |

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Service logic, OTP validation, token generation | JUnit 5 + Mockito |
| Unit | JwtProvider sign/verify | Mocked keys, edge cases |
| Integration | REST endpoints, DB round-trips | SpringBootTest + H2 (test profile) |
| Integration | Security filter chain (auth required/optional) | MockMvc + @WithMockUser |
| Coverage | All layers | JaCoCo > 80% |

## Migration / Rollout

No migration required (greenfield project). First `mvn clean install` compiles everything.

## Open Questions

- None resolved. OTP will use console mock; email integration deferred.
