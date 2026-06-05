# Utility Billing System (WASAC / REG)

A secure, automated **postpaid utility billing** backend built with **Spring Boot 3.5**,
**Spring Data JPA**, **Spring Security (JWT)** and **PostgreSQL**. It manages users &
roles, customers, meters, meter readings, tariffs, bills, payments and notifications,
enforces all business rules, and uses **database triggers** for messaging. Swagger UI
is the only interaction surface (no frontend).

## Tech stack
- Java 21 (compiled), runs on the installed JDK 26 with the ByteBuddy experimental flag
- Spring Boot 3.5.14 (Web, Data JPA, Security, Validation)
- PostgreSQL, Hibernate (`ddl-auto=update`)
- JWT via `io.jsonwebtoken:jjwt` 0.12.6
- OpenAPI/Swagger via `springdoc-openapi` 2.7.0

## 1. Prerequisites
- JDK on PATH (Java 26 present). Maven is **not** required — the project ships `mvnw`.
- PostgreSQL running locally.

## 2. Create the database
```sql
CREATE DATABASE utility_billing;
```
Then put your local secrets in an **untracked** `application-secret.properties` at the
project root (copy the example, which is gitignored so secrets never reach GitHub):
```powershell
Copy-Item application-secret.properties.example application-secret.properties
# edit it: spring.datasource.password=...  and  spring.mail.password=<gmail app password>
```

## 3. Run
```powershell
.\mvnw.cmd spring-boot:run
```
On first boot the app:
- creates all tables from the JPA entities,
- seeds the 4 roles and a default admin (`admin@utility.rw` / `Admin@123`),
- applies the PL/pgSQL triggers from `src/main/resources/db/routines.sql`.

> Java 26 note: the ByteBuddy experimental flag is wired into the Maven plugins. If you
> run the built jar directly, add `-Dnet.bytebuddy.experimental=true`.

## 4. Email (Gmail SMTP) — verification, reset & notifications
Set a Gmail **App Password** (Google Account → Security → 2-Step Verification → App
passwords) and provide it via environment variables before running:
```powershell
$env:MAIL_USERNAME = "rutagandasalim@gmail.com"
$env:MAIL_PASSWORD = "your-16-char-app-password"
```
Email is used for: **account-verification codes**, **password-reset codes** and
**bill/payment notifications**. To run without sending mail (offline/dev), set
`app.mail.enabled=false` in `application.properties` — the rest of the app works unchanged.

## 5. Seeded staff logins
On first boot these accounts are created (pre-verified). Customers self-register via signup.

| Role | Email | Password |
|------|-------|----------|
| ROLE_ADMIN | `admin@utility.rw` | `Admin@123` |
| ROLE_OPERATOR | `operator@utility.rw` | `Operator@123` |
| ROLE_FINANCE | `finance@utility.rw` | `Finance@123` |

## 6. Swagger UI
Open **http://localhost:8080/swagger-ui.html**
1. `POST /api/auth/login` with a seeded account → copy the `token`.
2. Click **Authorize**, paste the token → all secured endpoints are now callable.

**Auth endpoints** (grouped in Swagger as *Auth 1/2/3*)
| Endpoint | Purpose |
|----------|---------|
| `POST /api/auth/signup` | Register (roles optional; defaults to ROLE_CUSTOMER). **Auto-emails a verification code.** |
| `POST /api/auth/verify` | Verify email with the signup code → JWT |
| `POST /api/auth/verify/resend` | Resend the verification code |
| `POST /api/auth/code/resend` | Request a fresh code for any flow (`ACCOUNT_VERIFICATION` / `PASSWORD_RESET`) |
| `POST /api/auth/login` | **Email + password → JWT** (requires a verified email) |
| `POST /api/auth/password/forgot` | Email a password-reset code |
| `POST /api/auth/password/reset` | Reset password with the emailed code |
| `POST /api/auth/password/change` | Change password (authenticated) |

> **Login** is email + password only (no passwordless OTP login).
> **Email verification gate:** new signups start unverified and **cannot log in**
> until they confirm the code at `/api/auth/verify`. All emailed codes
> (verification, password reset) expire after **15 minutes**; request a fresh one any
> time at `/api/auth/code/resend` (it invalidates the previous code).

## 7. End-to-end demo (roles & business rules)
1. **Admin** — `POST /api/tariffs` (e.g. WATER, rate 500, service 1000, vat 18, penalty 5, effectiveFrom 2026-01-01).
2. **Admin/Operator** — `POST /api/customers`, then `POST /api/meters`.
3. **Operator** — `POST /api/readings` (current > previous; one per meter/month; meter active).
4. **Admin/Operator/Finance** — `POST /api/bills/generate` with the `meterReadingId`.
   → a **notification row is auto-created by the DB trigger** (`GET /api/notifications`).
5. **Admin/Finance** — `PATCH /api/bills/{id}/approve`.
6. **Finance** — `POST /api/payments` (partial then full). On full payment the balance
   hits 0, status → **PAID**, and the **full-payment trigger** posts another notification.
7. **Customer** — sign up / log in as a `ROLE_CUSTOMER` and use `GET /api/bills/me`,
   `GET /api/payments/me`, `GET /api/notifications/me`.

### Roles
| Role | Capabilities |
|------|--------------|
| ROLE_ADMIN | configure tariffs, approve bills, manage users |
| ROLE_OPERATOR | capture meter readings |
| ROLE_FINANCE | approve bills & record payments |
| ROLE_CUSTOMER | view own bills & payment history |

## 8. Diagrams
- ER diagram: `docs/erd.png` (source `docs/erd.mmd`)
- Spring Boot flow: `docs/flow.png` (source `docs/flow.mmd`)

Regenerate (stdlib only, renders via the Kroki service):
```powershell
python docs/render_diagrams.py
```

## 9. Database routines (Task 6)
`src/main/resources/db/routines.sql` defines two triggers on `bills`:
- **AFTER INSERT** → inserts a "bill processed" notification.
- **BEFORE UPDATE** (balance reaches 0) → marks the bill PAID and inserts a "payment received" notification.

Notification message format:
> Dear &lt;CustomerName&gt;, Your &lt;Month/Year&gt; utility bill of &lt;Amount&gt; FRW has been successfully processed.
