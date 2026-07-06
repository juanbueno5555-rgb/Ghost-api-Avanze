# Auth — Specification

## Overview

Authentication and authorization microservice for the Tech Cup tournament management system. Handles user registration, identity verification via OTP, JWT-based sessions, and role-based access control.

## Actors

| Actor | Description |
|-------|-------------|
| Student | Active student — registers with institutional email |
| Guest | External participant — registers with personal email, needs approval |
| Graduate | Alumni — registers with alumni email |
| Admin | Platform administrator — manages users, roles, and system config |
| Organizer | Tournament organizer — creates and manages tournaments |
| Captain | Team captain — manages team roster and match participation |
| Player | Team member — participates in matches |
| Referee | Official — manages live match events |

## Functional Requirements

### FUN-ID-01: User Registration

Users shall register with email, password, full name, document ID, and phone. Three registration flows:

| Flow | Identifier | Validation |
|------|-----------|------------|
| Student | `@estudiante. universidad.edu` domain | None (auto-approved) |
| Graduate | Alumni email or proof | Requires admin verification |
| Guest | Any other email | Requires admin verification or invitation code |

Password must be at least 8 characters with uppercase, lowercase, and number.

**Endpoint**: `POST /api/auth/register`
**Request**:
```json
{
  "email": "string",
  "password": "string",
  "fullName": "string",
  "documentId": "string",
  "phone": "string",
  "userType": "STUDENT | GRADUATE | GUEST",
  "invitationCode": "string? (optional, for GUEST)"
}
```
**Response** (201): `{ "message": "OTP sent to email", "otpToken": "string" }`
**Error codes**: 400 (validation), 409 (email exists)

### FUN-ID-02: OTP Verification

A 6-digit numeric code sent to the user's email after registration. Code is valid for 10 minutes.

**Endpoint**: `POST /api/auth/verify-otp`
**Request**:
```json
{
  "otpToken": "string",
  "code": "string (6 digits)"
}
```
**Response** (200):
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "email": "string",
    "fullName": "string",
    "roles": ["string"],
    "userType": "string",
    "verified": true
  }
}
```
**Error codes**: 400 (invalid/expired OTP), 429 (too many attempts)

### FUN-ID-03: Resend OTP

**Endpoint**: `POST /api/auth/resend-otp`
**Request**: `{ "otpToken": "string" }`
**Response** (200): `{ "message": "New OTP sent" }`
**Rate limit**: 1 resend per 60 seconds

### FUN-ID-04: Login

**Endpoint**: `POST /api/auth/login`
**Request**:
```json
{
  "email": "string",
  "password": "string"
}
```
**Response** (200): Same as FUN-ID-02 response (access + refresh tokens + user)
**Error codes**: 401 (invalid credentials), 403 (account not verified)

### FUN-ID-05: Token Refresh

**Endpoint**: `POST /api/auth/refresh`
**Request**: `{ "refreshToken": "string" }`
**Response** (200): `{ "accessToken": "string", "refreshToken": "string", "expiresIn": 900 }`
**Error codes**: 401 (invalid/expired refresh token)

### FUN-ID-06: Logout

**Endpoint**: `POST /api/auth/logout`
**Headers**: `Authorization: Bearer <accessToken>`
**Request**: `{ "refreshToken": "string" }`
**Response** (200): `{ "message": "Logged out" }`
Invalidates the refresh token server-side.

### FUN-ID-07: Get Current User

**Endpoint**: `GET /api/auth/me`
**Headers**: `Authorization: Bearer <accessToken>`
**Response** (200):
```json
{
  "id": "uuid",
  "email": "string",
  "fullName": "string",
  "documentId": "string",
  "phone": "string",
  "userType": "STUDENT | GRADUATE | GUEST",
  "roles": ["string"],
  "verified": true,
  "createdAt": "ISO datetime"
}
```

### FUN-ID-08: Role Management (Admin)

**Endpoint**: `PUT /api/auth/users/{id}/roles`
**Headers**: `Authorization: Bearer <accessToken>` (Admin only)
**Request**: `{ "roles": ["CAPTAIN", "REFEREE"] }`
**Response** (200): Updated user object
**Error codes**: 403 (not admin), 404 (user not found)

### FUN-ID-09: List Users (Admin)

**Endpoint**: `GET /api/auth/users?page=0&size=20&role=PLAYER&userType=STUDENT`
**Headers**: `Authorization: Bearer <accessToken>` (Admin only)
**Response** (200): Paginated user list

### FUN-ID-10: Password Reset

**Step 1** — Request reset: `POST /api/auth/reset-password`
**Request**: `{ "email": "string" }`
**Response** (200): `{ "message": "Reset code sent to email", "resetToken": "string" }`

**Step 2** — Confirm reset: `POST /api/auth/reset-password/confirm`
**Request**: `{ "resetToken": "string", "code": "string (6 digits)", "newPassword": "string" }`
**Response** (200): `{ "message": "Password updated" }`

## Roles

| Role | Authority | Description |
|------|-----------|-------------|
| `ADMIN` | Full system access | Manages users, roles, system config |
| `ORGANIZER` | Tournament management | Creates and manages tournaments |
| `CAPTAIN` | Team management | Manages team roster, lineups |
| `PLAYER` | Match participation | Default role for verified users |
| `REFEREE` | Match officiating | Controls live match events |
| `GUEST` | Limited access | Unverified or temporary users |

A user can have multiple roles. Default role on registration: `PLAYER` (verified) or `GUEST` (unverified).

## Data Model

### User Entity

| Field | Type | Notes |
|-------|------|-------|
| id | UUID (PK) | Auto-generated |
| email | String (unique) | Indexed |
| password | String | BCrypt hashed |
| fullName | String | |
| documentId | String (unique) | |
| phone | String | |
| userType | Enum: STUDENT, GRADUATE, GUEST | |
| roles | Set of Role enums | |
| verified | Boolean | true after OTP verification |
| enabled | Boolean | true by default |
| createdAt | LocalDateTime | |
| updatedAt | LocalDateTime | |

### OtpCode Entity

| Field | Type | Notes |
|-------|------|-------|
| id | UUID (PK) | |
| email | String | |
| code | String | 6-digit, hashed |
| type | Enum: VERIFICATION, PASSWORD_RESET | |
| expiresAt | LocalDateTime | TTL = 10 min |
| attempts | Integer | Max 5, then invalidate |
| used | Boolean | |

### RefreshToken Entity

| Field | Type | Notes |
|-------|------|-------|
| id | UUID (PK) | |
| userId | UUID (FK → User) | |
| token | String | Hashed |
| expiresAt | LocalDateTime | TTL = 7 days |
| revoked | Boolean | |

## API Contract

### Base Path: `/api/auth`

### Public Endpoints (no auth required)

| Method | Path | Handler |
|--------|------|---------|
| POST | `/api/auth/register` | register |
| POST | `/api/auth/verify-otp` | verifyOtp |
| POST | `/api/auth/resend-otp` | resendOtp |
| POST | `/api/auth/login` | login |
| POST | `/api/auth/refresh` | refreshToken |
| POST | `/api/auth/reset-password` | requestPasswordReset |
| POST | `/api/auth/reset-password/confirm` | confirmPasswordReset |

### Authenticated Endpoints

| Method | Path | Role | Handler |
|--------|------|------|---------|
| POST | `/api/auth/logout` | Any | logout |
| GET | `/api/auth/me` | Any | getCurrentUser |

### Admin Endpoints

| Method | Path | Role | Handler |
|--------|------|------|---------|
| PUT | `/api/auth/users/{id}/roles` | ADMIN | updateRoles |
| GET | `/api/auth/users` | ADMIN | listUsers |
| GET | `/api/auth/users/{id}` | ADMIN | getUserById |

## Security Constraints

- Passwords hashed with BCrypt (strength 10)
- OTP codes hashed with SHA-256 before storage
- Access token expiry: 15 minutes
- Refresh token expiry: 7 days
- Max 5 OTP attempts — auto-invalidate after
- JWT signed with HMAC-SHA256 (configurable secret via env)
- Rate limiting: 5 registration attempts per IP per minute
- CORS configurable via environment

## Non-Functional Requirements

- Swagger/OpenAPI documentation at `/swagger-ui.html`
- Integration tests with testcontainers (PostgreSQL)
- Unit test coverage > 80% (JaCoCo)
- Graceful handling of OTP expiry and concurrent requests
