# Proposal: Monorepo Base + Auth Microservice

## Intent

Set up the monorepo structure for all 12 Ghost API microservices and implement the Auth microservice (registration, OTP 2FA, JWT, roles). This establishes the foundation that every other service will build on.

## Scope

### In Scope
- Parent POM with dependency management (Spring Boot 3.2, Java 17)
- `common/` library (shared DTOs, JWT utils, error handling, base entities)
- `auth-service/` — complete microservice with:
  - Student / Guest / Graduate registration
  - OTP 2FA via email
  - JWT access + refresh tokens
  - Role-based auth (ADMIN, ORGANIZER, CAPTAIN, PLAYER, REFEREE, GUEST)
- `.gitignore`, `docker-compose.yml` (local dev with PostgreSQL)
- CI/CD base config (GitHub Actions — compile + test + JaCoCo)
- OpenAPI/Swagger (springdoc-openapi)

### Out of Scope
- Other 11 microservices (Teams, Tournaments, Competition, etc.)
- Real email sending (OTP will use console-logged mock)
- Frontend integration
- Deployment to QA/PROD
- API Gateway routing

## Capabilities

### New Capabilities
- `auth`: Authentication and authorization — registration, OTP verification, JWT-based login, role management

### Modified Capabilities
- None (greenfield project)

## Approach

Standard Spring Boot monorepo with Maven multi-module:

```
backend-ghost-api/
├── pom.xml                    # Parent POM (Spring Boot starter parent, dependency management)
├── common/                    # Shared library (no Spring Boot, plain Java jar)
│   └── pom.xml
├── auth-service/              # Spring Boot microservice
│   └── pom.xml
├── (future services...)
├── docker-compose.yml         # PostgreSQL, later more infra
└── .github/workflows/ci.yml   # Build + test pipeline
```

Auth service architecture:
- **Controller layer**: REST endpoints (AuthController, UserController)
- **Service layer**: AuthService (registration, login, OTP, token management)
- **Repository layer**: Spring Data JPA (UserRepository, OtpRepository)
- **Security**: Spring Security with JWT filter chain
- **DTOs**: Request/response records in the service itself (plus shared ones in `common/`)
- **OTP**: 6-digit code, stored with expiry, sent via console logger (mock)

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `pom.xml` | New | Parent POM defining all modules and dependency versions |
| `common/` | New | Shared library |
| `auth-service/` | New | Complete auth microservice |
| `docker-compose.yml` | New | Local dev infrastructure |
| `.gitignore` | New | Java/Maven/IDE ignores |
| `.github/workflows/ci.yml` | New | CI pipeline |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| OTP mock needs real email later | Med | OTP service is interface-based — swap impl later |
| JWT secret hardcoded in dev | Low | Externalize to env vars / docker secrets |
| Common lib becomes dumping ground | Med | Enforce common/ only for truly shared code |

## Rollback Plan

Remove the `auth-service/` module, `common/` module, `pom.xml` changes, and `docker-compose.yml`. Revert to README-only state via `git revert`.

## Dependencies

- Java 17+ SDK
- Maven 3.9+
- Docker Desktop (for PostgreSQL via docker-compose)

## Success Criteria

- [ ] `mvn clean compile` succeeds for all modules
- [ ] `mvn test` passes with unit + integration tests
- [ ] Auth service starts with `mvn spring-boot:run`
- [ ] POST `/api/auth/register` creates user + sends OTP
- [ ] POST `/api/auth/verify-otp` returns JWT on valid OTP
- [ ] POST `/api/auth/login` returns JWT with correct roles
- [ ] Swagger UI available at `/swagger-ui.html`
