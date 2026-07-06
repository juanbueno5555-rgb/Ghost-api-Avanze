# Tasks: Monorepo Base + Auth Microservice

> Estimated files: ~45 files
> Estimated changed lines: ~2,500
> Review workload: HIGH — recommended to split into chained PRs

## Phase 1: Monorepo Foundation (5 files, ~150 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 1.1 | Create parent POM with Spring Boot 3.2.3, Java 17, dependency management, and module declarations | `pom.xml` | `mvn clean compile` succeeds |
| 1.2 | Create `.gitignore` for Java/Maven/IDE/OS | `.gitignore` | `git status` shows clean ignores |
| 1.3 | Create `docker-compose.yml` with PostgreSQL (port 5432, db `techcup_auth`) | `docker-compose.yml` | `docker compose up -d` starts PostgreSQL |
| 1.4 | Create CI workflow `.github/workflows/ci.yml` — build + test + JaCoCo | `.github/workflows/ci.yml` | Workflow parses correctly |
| 1.5 | Create `common/` Maven module with shared base exception class and DTO base | `common/pom.xml`, `common/src/...` | `mvn compile` includes common |

## Phase 2: Auth Domain Model (6 files, ~200 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 2.1 | Create `Role` enum (ADMIN, ORGANIZER, CAPTAIN, PLAYER, REFEREE, GUEST) | `auth-service/.../model/Role.java` | Compiles, test coverage |
| 2.2 | Create `User` entity with all fields + relationships | `auth-service/.../model/User.java` | Compiles, JPA annotated |
| 2.3 | Create `OtpCode` entity with expiry + attempts tracking | `auth-service/.../model/OtpCode.java` | Compiles, TTL logic |
| 2.4 | Create `RefreshToken` entity with revocation support | `auth-service/.../model/RefreshToken.java` | Compiles, indexed |
| 2.5 | Create Spring Data repositories for all 3 entities | `auth-service/.../repository/*Repository.java` | Beans injectable |
| 2.6 | Create `application.yml` with datasource, JPA, JWT config, server port 8081 | `auth-service/.../application.yml` | Service starts |

## Phase 3: Security Infrastructure (4 files, ~250 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 3.1 | Create `JwtProvider` — generate access + refresh tokens, validate, extract claims | `auth-service/.../security/JwtProvider.java` | Unit tests pass |
| 3.2 | Create `JwtAuthenticationFilter` — extract JWT from header, validate, set SecurityContext | `auth-service/.../security/JwtAuthenticationFilter.java` | Integration test with MockMvc |
| 3.3 | Create `CustomUserDetailsService` — load user by email/ID | `auth-service/.../security/CustomUserDetailsService.java` | Spring Security integration |
| 3.4 | Create `SecurityConfig` — filter chain, permit public endpoints, role-based access | `auth-service/.../config/SecurityConfig.java` | Auth required/optional tested |

## Phase 4: Auth Service Layer (5 files, ~350 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 4.1 | Create `OtpService` — generate, validate, hash OTP codes, enforce max attempts | `auth-service/.../service/OtpService.java` | Unit tests pass |
| 4.2 | Create `EmailService` interface + `ConsoleEmailService` mock impl | `auth-service/.../service/EmailService.java` | OTP printed to console |
| 4.3 | Create `TokenService` — issue, refresh, revoke JWT + refresh tokens | `auth-service/.../service/TokenService.java` | Unit tests pass |
| 4.4 | Create `AuthService` — register, verify OTP, login, logout, password reset | `auth-service/.../service/AuthService.java` | Full flow tests pass |
| 4.5 | Create DTOs: request/response records for all endpoints | `auth-service/.../dto/request/*`, `dto/response/*` | Compiles, serializes correctly |

## Phase 5: Auth Controller Layer (5 files, ~300 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 5.1 | Create `AuthController` — register, verify-otp, resend-otp, login, refresh, logout | `auth-service/.../controller/AuthController.java` | All endpoints respond |
| 5.2 | Create `AdminController` — list users, get user, update roles | `auth-service/.../controller/AdminController.java` | Admin-only tested |
| 5.3 | Create `GlobalExceptionHandler` — consistent error response format | `auth-service/.../exception/GlobalExceptionHandler.java` | Error format consistent |
| 5.4 | Create `OpenApiConfig` — Swagger/OpenAPI configuration | `auth-service/.../config/OpenApiConfig.java` | `/swagger-ui.html` loads |
| 5.5 | Create `PasswordResetController` (or integrate into AuthController) — request + confirm reset | `auth-service/.../controller/AuthController.java` (extend) | Reset flow tested |

## Phase 6: Tests (6+ files, ~500 lines)

| # | Task | Files | Verification |
|---|------|-------|-------------|
| 6.1 | Write unit tests for `JwtProvider` | `auth-service/src/test/.../JwtProviderTest.java` | Edge cases covered |
| 6.2 | Write unit tests for `OtpService` | `auth-service/src/test/.../OtpServiceTest.java` | Expiry + attempts tested |
| 6.3 | Write unit tests for `AuthService` | `auth-service/src/test/.../AuthServiceTest.java` | All registration flows |
| 6.4 | Write integration tests for `AuthController` endpoints | `auth-service/src/test/.../AuthControllerIntegrationTest.java` | MockMvc full flow |
| 6.5 | Write integration tests for security (auth required, role-based) | `auth-service/src/test/.../SecurityIntegrationTest.java` | 401/403 cases |
| 6.6 | Configure JaCoCo + verify coverage > 80% | `auth-service/pom.xml` (plugin config) | `mvn verify` shows report |

## Delivery Forecast

| Metric | Estimate |
|--------|----------|
| Total new files | ~35 |
| Total changed lines | ~2,500 |
| Test files | ~6 |
| Test coverage | >80% |
| Chained PRs recommended | **Yes** — split into 2-3 PRs |

## Decision needed before apply
- [x] Project structure confirmed
- [x] Tech stack confirmed
- [x] Auth spec reviewed
- [ ] **Delivery strategy**: single PR or chained PRs? (2,500 lines exceeds 800-line budget)
