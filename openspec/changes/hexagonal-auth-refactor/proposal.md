# Proposal: Hexagonal Auth Refactor

## Intent

Migrate the auth-service from layered architecture (controller → service → repository) to Hexagonal Architecture (Ports & Adapters). This aligns with the professor's requirement and establishes the pattern for all future microservices.

## Scope

### In Scope
- Restructure `auth-service/` into `domain/`, `application/`, `infrastructure/input/`, `infrastructure/output/`
- Extract pure domain POJOs (no JPA annotations) from current `model/`
- Create separate JPA entity classes in `infrastructure/output/jpa/entity/`
- Convert existing Spring Data repos into port interfaces + JPA adapter implementations
- Move business logic into `domain/service/` (use cases) with port dependencies
- Keep all DTOs in `application/dto/`
- Keep all REST controllers in `infrastructure/input/rest/`
- Keep security (JWT, filters, config) in `infrastructure/input/security/`
- Keep ExceptionHandler in `infrastructure/input/`
- All 17 existing tests MUST still pass
- No behavioral changes — pure structural refactor

### Out of Scope
- New features or endpoints
- Changing the common/ module
- API Gateway or other services
- Test restructuring (tests stay in same packages, same logic)

## Capabilities

### New Capabilities
- None (same auth behavior)

### Modified Capabilities
- `auth` — Same requirements, new internal structure

## Approach

Current structure → Target structure:

```
Before:                              After:
com.techcup.auth/                    com.techcup.auth/
├── model/ (JPA entities)           ├── domain/
├── repository/ (Spring Data)        │   ├── model/ (pure POJOs)
├── service/ (business logic)        │   ├── port/input/ (use case interfaces)
├── controller/ (REST)               │   ├── port/output/ (repository interfaces)
├── security/ (JWT, filters)         │   └── service/ (use case impl, pure logic)
├── config/                          ├── application/dto/
├── dto/                             └── infrastructure/
└── exception/                           ├── input/
                                         │   ├── rest/ (controllers)
                                         │   ├── security/ (JWT, filters, config)
                                         │   └── exception/ (handler)
                                         └── output/
                                             ├── jpa/
                                             │   ├── entity/ (JPA entities)
                                             │   ├── repository/ (Spring Data impl)
                                             │   └── mapper/ (domain ↔ entity)
                                             └── email/ (adapters)
```

Strategy: Create new package structure alongside old, move files one by one, delete old packages at end. No behavioral changes.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `auth-service/` | Major restructure | All packages reorganized into hexagonal layout |
| Existing tests | Modified imports | Package imports change, test logic stays same |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Tests break during refactor | Medium | Run `mvn test` after each phase |
| Mapper bugs (domain ↔ entity) | Medium | Test coverage on critical paths |
| Forgot to delete old files | Low | `git diff` review at end |

## Rollback Plan

`git revert` the refactor commit. The old structure still works.

## Dependencies

- Current auth-service compiling and tested (baseline commit `8f830b9`)

## Success Criteria

- [ ] `mvn clean test` — 17/17 tests pass
- [ ] No `@Entity` or JPA annotations in `domain/` package
- [ ] No Spring imports in any `domain/` file
- [ ] All old layered packages removed
