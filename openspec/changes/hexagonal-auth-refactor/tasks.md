# Tasks: Hexagonal Auth Refactor

> Estimated changed lines: ~800 (new files + deletions)
> Review workload: MEDIUM — structural refactor, no new logic

## Phase 1: Domain Model + Ports

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 1.1 | Create `domain/model/User.java` — pure POJO with all fields, no annotations | `domain/model/User.java` | No imports from jakarta.* or Spring |
| 1.2 | Create `domain/model/OtpCode.java` — pure POJO | `domain/model/OtpCode.java` | Same purity check |
| 1.3 | Create `domain/model/RefreshToken.java` — pure POJO | `domain/model/RefreshToken.java` | Same purity check |
| 1.4 | Create `domain/model/Role.java` — enum (same as current) | `domain/model/Role.java` | Same values |
| 1.5 | Create `domain/model/UserType.java` — enum | `domain/model/UserType.java` | Same values |
| 1.6 | Create `domain/port/output/UserRepository.java` — interface | `domain/port/output/UserRepository.java` | No Spring Data extends |
| 1.7 | Create `domain/port/output/OtpCodeRepository.java` — interface | `domain/port/output/OtpCodeRepository.java` | No Spring Data extends |
| 1.8 | Create `domain/port/output/RefreshTokenRepository.java` — interface | `domain/port/output/RefreshTokenRepository.java` | No Spring Data extends |
| 1.9 | Create `domain/port/output/EmailSender.java` — interface | `domain/port/output/EmailSender.java` | sendOtp(email, code) |
| 1.10 | Create `domain/port/input/AuthUseCase.java` — interface | `domain/port/input/AuthUseCase.java` | All auth operations |
| 1.11 | Create `domain/port/input/AdminUseCase.java` — interface | `domain/port/input/AdminUseCase.java` | User management operations |

**Verify**: `mvn compile` — no compilation errors in domain package

## Phase 2: Output Adapters (JPA)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 2.1 | Create `UserEntity.java` — @Entity class (copied from current User.java) | `infrastructure/output/jpa/entity/UserEntity.java` | All JPA annotations |
| 2.2 | Create `OtpCodeEntity.java` — @Entity class | `infrastructure/output/jpa/entity/OtpCodeEntity.java` | All JPA annotations |
| 2.3 | Create `RefreshTokenEntity.java` — @Entity class | `infrastructure/output/jpa/entity/RefreshTokenEntity.java` | All JPA annotations |
| 2.4 | Create `SpringDataUserRepository` — extends JpaRepository | `infrastructure/output/jpa/repository/SpringDataUserRepository.java` | Standard Spring Data |
| 2.5 | Create `SpringDataOtpCodeRepository` | `infrastructure/output/jpa/repository/SpringDataOtpCodeRepository.java` | Standard Spring Data |
| 2.6 | Create `SpringDataRefreshTokenRepository` | `infrastructure/output/jpa/repository/SpringDataRefreshTokenRepository.java` | Standard Spring Data |
| 2.7 | Create `UserMapper.java` — toDomain() + toEntity() | `infrastructure/output/jpa/mapper/UserMapper.java` | Bidirectional mapping |
| 2.8 | Create `OtpCodeMapper.java` | `infrastructure/output/jpa/mapper/OtpCodeMapper.java` | Bidirectional mapping |
| 2.9 | Create `RefreshTokenMapper.java` | `infrastructure/output/jpa/mapper/RefreshTokenMapper.java` | Bidirectional mapping |
| 2.10 | Create `JpaUserRepositoryAdapter` — implements domain UserRepository | `infrastructure/output/adapter/JpaUserRepositoryAdapter.java` | Implements domain port |
| 2.11 | Create `JpaOtpCodeRepositoryAdapter` | `infrastructure/output/adapter/JpaOtpCodeRepositoryAdapter.java` | Implements domain port |
| 2.12 | Create `JpaRefreshTokenRepositoryAdapter` | `infrastructure/output/adapter/JpaRefreshTokenRepositoryAdapter.java` | Implements domain port |
| 2.13 | Create `ConsoleEmailAdapter` — implements EmailSender | `infrastructure/output/email/ConsoleEmailAdapter.java` | Console mock |

**Verify**: `mvn compile` — all adapters wired

## Phase 3: Domain Services (Use Cases)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 3.1 | Create `AuthService` in domain/service — implements AuthUseCase, injects port interfaces | `domain/service/AuthService.java` | Same logic as current, imports only domain + Java stdlib |
| 3.2 | Create `AdminService` in domain/service | `domain/service/AdminService.java` | Same logic, pure domain |

**Verify**: `mvn compile` — services only depend on domain ports + model

## Phase 4: Move Infrastructure Input

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 4.1 | Move `AuthController` to `infrastructure/input/rest/`, update imports | Move file | Compiles, injection of AuthUseCase |
| 4.2 | Move `AdminController` to `infrastructure/input/rest/` | Move file | Same |
| 4.3 | Move `JwtProvider` to `infrastructure/input/security/` | Move file | Same |
| 4.4 | Move `JwtAuthenticationFilter` to `infrastructure/input/security/` | Move file | Same |
| 4.5 | Move `CustomUserDetailsService` to `infrastructure/input/security/` | Move file | Same |
| 4.6 | Move `SecurityConfig` to `infrastructure/input/security/config/` | Move file | Same |
| 4.7 | Move `OpenApiConfig` to `infrastructure/input/security/config/` | Move file | Same |
| 4.8 | Move `GlobalExceptionHandler` to `infrastructure/input/exception/` | Move file | Same |

**Verify**: `mvn test` — all tests pass with new imports

## Phase 5: Cleanup + Verify

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 5.1 | Delete old `model/` package | `auth-service/src/main/java/com/techcup/auth/model/` | Nothing references old location |
| 5.2 | Delete old `repository/` package | `auth-service/src/main/java/com/techcup/auth/repository/` | Nothing references old location |
| 5.3 | Delete old `service/` package | `auth-service/src/main/java/com/techcup/auth/service/` | Nothing references old location |
| 5.4 | Delete old `dto/` package | `auth-service/src/main/java/com/techcup/auth/dto/` | Moved to application/dto/ |
| 5.5 | Delete old `controller/` package | `auth-service/src/main/java/com/techcup/auth/controller/` | Moved |
| 5.6 | Delete old `security/` package | `auth-service/src/main/java/com/techcup/auth/security/` | Moved |
| 5.7 | Delete old `config/` package | `auth-service/src/main/java/com/techcup/auth/config/` | Moved |
| 5.8 | Delete old `exception/` package | `auth-service/src/main/java/com/techcup/auth/exception/` | Moved |
| 5.9 | Update test imports to match new package locations | All test files | `mvn test` passes |

**Final verify**: `mvn clean test` — 17/17 tests pass

## Delivery

| Metric | Estimate |
|--------|----------|
| Files created | ~25 new files |
| Files deleted/moved | ~15 |
| Behavioral changes | ZERO |
| Test risk | Low — same logic, new packages |
