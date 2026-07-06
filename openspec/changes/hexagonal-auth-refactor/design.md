# Design: Hexagonal Auth Refactor

## Technical Approach

Refactor in 4 sequential phases, each maintaining `mvn test` passing:

1. **Create domain model**: Pure POJOs + port interfaces (no framework deps)
2. **Create output adapters**: JPA entities, Spring Data repos, mappers
3. **Refactor services**: Extract use cases, wire through ports
4. **Move controllers/security**: To infrastructure/input/, update imports

## Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Domain purity | No Spring, no JPA, no Lombok in domain | Strict hexagonal — domain must be framework-agnostic |
| Entity mapping | Manual mapper (no MapStruct) | Evita dependencia extra, el mapper es simple (User ↔ UserEntity) |
| Domain identifiers | `UserId` value object vs raw UUID | Raw UUID is fine for now — Value Object añade complejidad sin beneficio claro |
| Transaction boundary | `@Transactional` en adaptador de salida JPA | La transacción es preocupación de infraestructura, no del dominio |
| Validation | Jakarta Validation en DTOs de aplicación | Domain validation via constructor/static factories |

## Package Blueprint

```
auth-service/src/main/java/com/techcup/auth/
│
├── AuthApplication.java           (se queda igual)
│
├── domain/                        ← ZERO framework dependencies
│   ├── model/
│   │   ├── User.java              POJO: id, email, password, fullName, etc. NO @Entity
│   │   ├── OtpCode.java           POJO: id, email, code, expiresAt, attempts, used
│   │   ├── RefreshToken.java      POJO: id, userId, token, expiresAt, revoked
│   │   ├── Role.java              Enum (same)
│   │   └── UserType.java          Enum (same)
│   │
│   ├── port/
│   │   ├── input/
│   │   │   ├── AuthUseCase.java           — register(), verifyOtp(), login(), etc.
│   │   │   └── AdminUseCase.java          — listUsers(), updateRoles()
│   │   └── output/
│   │       ├── UserRepository.java        — findByEmail(), save(), existsByEmail()
│   │       ├── OtpCodeRepository.java     — findByIdAndEmail(), save()
│   │       ├── RefreshTokenRepository.java— findByToken(), save(), deleteByUserId()
│   │       └── EmailSender.java           — sendOtp(email, code)
│   │
│   └── service/
│       ├── AuthService.java       Implements AuthUseCase (pure business logic)
│       └── AdminService.java      Implements AdminUseCase
│
├── application/
│   └── dto/
│       ├── request/               (mismos DTOs, se mudan acá)
│       └── response/              (mismos DTOs, se mudan acá)
│
└── infrastructure/
    ├── input/
    │   ├── rest/
    │   │   ├── AuthController.java         (misma lógica, importa AuthUseCase)
    │   │   └── AdminController.java
    │   ├── security/
    │   │   ├── JwtProvider.java            (se queda igual)
    │   │   ├── JwtAuthenticationFilter.java(se queda igual)
    │   │   ├── CustomUserDetailsService.java(se queda igual)
    │   │   └── config/
    │   │       ├── SecurityConfig.java
    │   │       └── OpenApiConfig.java
    │   └── exception/
    │       └── GlobalExceptionHandler.java (se queda igual, se mueve acá)
    │
    └── output/
        ├── jpa/
        │   ├── entity/
        │   │   ├── UserEntity.java         — @Entity, @Table, JPA annotations
        │   │   ├── OtpCodeEntity.java      — @Entity
        │   │   └── RefreshTokenEntity.java — @Entity
        │   ├── repository/
        │   │   ├── SpringDataUserRepository.java     — extends JpaRepository, no @Domain
        │   │   ├── SpringDataOtpCodeRepository.java
        │   │   └── SpringDataRefreshTokenRepository.java
        │   └── mapper/
        │       ├── UserMapper.java         — UserEntity ↔ User
        │       ├── OtpCodeMapper.java      — OtpCodeEntity ↔ OtpCode
        │       └── RefreshTokenMapper.java — RefreshTokenEntity ↔ RefreshToken
        │
        ├── adapter/
        │   ├── JpaUserRepositoryAdapter.java         — implements domain UserRepository
        │   ├── JpaOtpCodeRepositoryAdapter.java      — implements domain OtpCodeRepository
        │   └── JpaRefreshTokenRepositoryAdapter.java — implements domain RefreshTokenRepository
        │
        └── email/
            ├── EmailSenderAdapter.java     — implements EmailSender (delegates to console or real)
            └── ConsoleEmailAdapter.java
```

## Data Flow (Hexagonal)

```
[HTTP Request]
      ↓
infrastructure/input/rest/AuthController   ← Adaptador de entrada
      ↓  llama a puerto de entrada (interfaz)
domain/service/AuthService (implements AuthUseCase)   ← Caso de uso
      ↓  llama a puerto de salida (interfaz)
domain/port/output/UserRepository          ← Puerto (contrato)
      ↓  implementación concreta
infrastructure/output/adapter/JpaUserRepositoryAdapter  ← Adaptador de salida
      ↓  delega a
infrastructure/output/jpa/repository/SpringDataUserRepository  ← JPA
      ↓
[Database]
```

## Entity Mapping Strategy

```
UserEntity (JPA)          UserMapper          User (Domain)
─────────────────         ──────────          ─────────────
id: UUID @Id       ──→    .toDomain()   ──→  id: UUID
email: String      ──→                      email: String
password: String   ──→                      password: String
fullName: String   ──→                      fullName: String
—                  ←──    .toEntity()   ←──  (no JPA annotations)
role: String @Enumerated
```

The mapper is a simple stateless Spring `@Component` with two methods: `toDomain()` and `toEntity()`.

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `model/*.java` (5 files) | Delete | Moved to domain/model/ as pure POJOs |
| `repository/*.java` (3 files) | Delete | Replaced by domain ports + JPA adapters |
| `service/*.java` (5 files) | Delete | Replaced by domain/services |
| `controller/*.java` (2 files) | Move | To infrastructure/input/rest/ |
| `security/*.java` (3 files) | Move | To infrastructure/input/security/ |
| `config/*.java` (2 files) | Move | To infrastructure/input/security/config/ |
| `exception/*.java` (1 file) | Move | To infrastructure/input/exception/ |
| `dto/*.java` (9 files) | Move | To application/dto/ |
| `domain/model/*.java` (5 files) | Create | Pure POJOs from current model/ |
| `domain/port/input/AuthUseCase.java` | Create | Use case interface |
| `domain/port/input/AdminUseCase.java` | Create | Use case interface |
| `domain/port/output/*.java` (4 files) | Create | Repository + email interfaces |
| `domain/service/AuthService.java` | Create | Pure business logic |
| `domain/service/AdminService.java` | Create | Pure business logic |
| `infrastructure/output/jpa/entity/*.java` (3 files) | Create | JPA entity classes |
| `infrastructure/output/jpa/repository/*.java` (3 files) | Create | Spring Data repos |
| `infrastructure/output/jpa/mapper/*.java` (3 files) | Create | Domain ↔ Entity mapping |
| `infrastructure/output/adapter/*.java` (3 files) | Create | Port implementations |
| `infrastructure/output/email/*.java` (2 files) | Create | Email adapters |

Total: ~20 files created, ~10 moved, ~13 deleted

## Migration / Rollout

**Phase 1**: Create domain model + ports (pure POJOs, interfaces)
**Phase 2**: Create JPA entities + mappers + adapters (infrastructure/output)
**Phase 3**: Rewire services (domain/service implements domain/port/input)
**Phase 4**: Move controllers, security, config, exception to infrastructure/input/
**Phase 5**: Delete old packages, update test imports, verify all tests pass

Run `mvn test` after EACH phase to catch regressions early.

## Open Questions

- None. Structure is clear. Begin with Phase 1.
