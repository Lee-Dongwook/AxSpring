# axspring Backend

Spring Boot 기반 백엔드를 직접 스캐폴딩하면서 헥사고날 아키텍처를 적용해
나가는 프로젝트입니다.

현재 프론트엔드가 먼저 존재하는 프로젝트를 기준으로 백엔드를 단계적으로
구축하고 있으며, 초기부터 모든 구조를 과도하게 만들기보다 실제
유스케이스가 생기는 순서대로 도메인, 포트, 어댑터를 추가합니다.

## Architecture Direction

기본 방향은 Hexagonal Architecture입니다.

```text
Web Adapter
    ↓
Inbound Port
    ↓
Application Service
    ↓
Domain
    ↓
Outbound Port
    ↓
Persistence / External Adapter
```

주요 원칙:

- Domain은 Spring, JPA, HTTP를 모른다.
- Controller는 Repository를 직접 호출하지 않는다.
- Application은 구체적인 DB/JPA/BCrypt 구현을 모른다.
- 외부 기술은 Adapter에서 구현한다.
- DB FK와 도메인 객체 연관관계는 별개의 문제로 판단한다.
- 처음부터 모든 추상화를 만들지 않고 실제 요구가 생길 때 확장한다.

---

## 1. Global HTTP Configuration

### CORS

`CorsConfigurationSource`를 Bean으로 등록해 전역 CORS 정책을 관리합니다.

주요 정책:

- 허용 Origin은 설정값으로 관리
- `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` 허용
- Credentials 허용
- 모든 API 경로에 적용

구조 예시:

```text
global/
└── config/
    ├── CorsConfig.java
    └── CorsProperties.java
```

### Request ID

각 HTTP 요청에 `X-Request-Id`를 부여합니다.

클라이언트가 `X-Request-Id`를 전달하면 해당 값을 사용하고, 없으면
서버에서 생성합니다.

Request ID는 MDC에도 등록하여 애플리케이션 로그를 요청 단위로 추적할 수
있게 합니다.

```text
global/
└── filter/
    └── RequestIdFilter.java
```

응답 예시:

```http
X-Request-Id: b59af087-5302-4cff-b598-23dfafac0824
```

### Access Logging

`AccessLoggingFilter`를 별도로 두어 다음 정보를 기록하는 구조로
설계했습니다.

```text
method
path
status
duration
requestId
```

Request body, password, token 등 민감 정보는 기본 access log에 기록하지
않습니다.

필터 순서는 다음을 의도합니다.

```text
Request
  ↓
RequestIdFilter
  ↓
AccessLoggingFilter
  ↓
Application
  ↑
AccessLoggingFilter
  ↑
RequestIdFilter
  ↑
Response
```

---

## 2. Global Error Handling

전역 HTTP 에러 응답을 통일합니다.

```text
global/
└── error/
    ├── ErrorCode.java
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
```

현재 기본 응답 형태:

```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "서버 내부 오류가 발생했습니다.",
  "requestId": "..."
}
```

현재 기본 Error Code:

- `INVALID_REQUEST`
- `INTERNAL_SERVER_ERROR`

예상 가능한 validation 오류와 예상하지 못한 서버 오류를 구분합니다.

500 계열 예외는 서버 로그에 stack trace를 남기되 내부 예외 내용을
클라이언트 응답에 그대로 노출하지 않습니다.

### Validation

Spring Validation을 사용합니다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

`MethodArgumentNotValidException`을 `GlobalExceptionHandler`에서
처리하여 `INVALID_REQUEST` 응답으로 변환합니다.

입력 형식 검증과 비즈니스 규칙은 분리합니다.

```text
@NotBlank / @Email / @Size
→ HTTP 입력 검증

중복 이메일 / 주문 상태 / 권한 등의 규칙
→ Domain / Application 규칙
```

---

## 3. User Domain

첫 번째 주요 도메인은 User입니다.

기존 프론트엔드/Prisma 모델을 기준으로 relation을 제외한 사용자 속성을
먼저 모델링합니다.

```text
user/
└── domain/
    ├── User.java
    ├── UserId.java
    ├── Email.java
    └── UserRole.java
```

### User Properties

현재 relation 제외 기준:

```text
id
name
email
emailVerifiedAt
imageUrl
passwordHash
role
department
position
slackUserId
googleAccountId
notionPersonId
linearUserId
githubLogin
emailAliases
hireDate
birthDate
active
mustChangePassword
passwordChangedAt
createdAt
updatedAt
```

타입 방향:

```text
UserId          → String 기반 Value Object
Email           → Email Value Object
emailAliases    → List<Email>
hireDate        → LocalDate
birthDate       → LocalDate
시간 기록       → Instant
```

`emailAliases`는 DB에서는 JSONB로 저장할 예정이지만 Domain에서는
JSON/Jackson 타입을 사용하지 않습니다.

### UserRole

현재 역할:

```java
public enum UserRole {
    OWNER,
    ADMIN,
    MANAGER,
    MEMBER,
    VIEWER
}
```

신규 회원의 기본 Role은 `MEMBER`입니다.

Role의 선언 순서(`ordinal`)를 권한 서열로 사용하지 않습니다.

### Domain Behavior

User는 무분별한 setter 대신 행위를 표현하는 메서드를 사용하는
방향입니다.

예:

```text
User.register(...)
user.verifyEmail(...)
user.changePassword(...)
user.deactivate(...)
user.updateProfile(...)
user.connectSlack(...)
user.connectGoogle(...)
...
```

신규 회원 생성과 DB에서 기존 회원을 복원하는 과정은 별개의 생성 경로로
취급할 예정입니다. Persistence 구현 시 복원 전략을 추가합니다.

---

## 4. Register User Use Case

첫 Application Use Case로 회원 등록을 구현했습니다.

```text
user/
├── domain/
│
└── application/
    ├── port/
    │   ├── in/
    │   │   ├── RegisterUserUseCase.java
    │   │   └── RegisterUserCommand.java
    │   └── out/
    │       ├── UserRepository.java
    │       └── PasswordEncoder.java
    └── service/
        └── RegisterUserService.java
```

흐름:

```text
RegisterUserCommand
        ↓
RegisterUserUseCase
        ↓
RegisterUserService
        ↓
Email 중복 확인
        ↓
Password Encoding
        ↓
User.register(...)
        ↓
UserRepository.save(...)
```

Controller의 HTTP Request DTO를 Application 계층까지 전달하지 않고
`RegisterUserCommand`로 변환하는 구조를 사용합니다.

### Outbound Ports

`UserRepository`는 JPA Repository가 아니라 Application이 필요로 하는
persistence capability입니다.

예:

```java
boolean existsByEmail(Email email);

User save(User user);
```

`PasswordEncoder` 역시 Spring Security 타입에 직접 의존하지 않는
Port입니다.

```java
String encode(String rawPassword);

boolean matches(String rawPassword, String encodedPassword);
```

---

## 5. BCrypt Adapter

PasswordEncoder Port의 첫 Outbound Adapter를 구현했습니다.

```text
user/
└── adapter/
    └── out/
        └── security/
            └── BCryptPasswordEncoderAdapter.java
```

의존성:

```gradle
implementation 'org.springframework.security:spring-security-crypto'
```

구조:

```text
RegisterUserService
        ↓
PasswordEncoder
        ↑
BCryptPasswordEncoderAdapter
        ↓
Spring Security Crypto
```

Application 계층은 BCrypt라는 구체적인 해싱 기술을 알지 않습니다.

---

## 6. Testing Strategy

테스트 기반은 JUnit 5 + AssertJ + Mockito를 사용합니다.

```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
```

테스트 전략:

```text
Domain
→ 순수 JUnit + AssertJ

Application Service
→ JUnit + Mockito
→ Spring Context 사용하지 않음

Web Adapter
→ MockMvc / Web MVC Slice Test

Persistence Adapter
→ JPA Slice + PostgreSQL 기반 테스트

전체 통합
→ @SpringBootTest
```

모든 테스트에 `@SpringBootTest`를 사용하는 방식은 피합니다.

현재 이후 테스트 코드 작성은 주 개발 흐름과 분리하고, 프로덕션 코드와
아키텍처 구현에 집중합니다.

---

## 7. PostgreSQL Development Environment

로컬 PostgreSQL을 직접 설치하지 않고 Docker Compose로 실행합니다.

Spring Boot 애플리케이션은 현재 호스트에서 실행하고 PostgreSQL만
Docker에서 실행합니다.

```text
Spring Boot (Host)
      ↓
localhost:15432
      ↓
Docker
      ↓
PostgreSQL :5432
```

컨테이너 내부 PostgreSQL 포트는 기본 `5432`를 유지하고, 호스트 노출
포트는 다른 프로젝트와 충돌을 피하기 위해 `15432`를 사용합니다.

### `.env`

프로젝트 루트:

```dotenv
DB_NAME=axspring
DB_USERNAME=axspring
DB_PASSWORD=your-local-password
DB_PORT=15432
REDIS_PORT=16379
REDIS_PASSWORD=your-local-redis-password
JWT_PRIVATE_KEY_PATH=secrets/jwt-private.pem
JWT_PUBLIC_KEY_PATH=secrets/jwt-public.pem
```

`.env`는 Git에 커밋하지 않습니다.

### `.env.example`

```dotenv
DB_NAME=axspring
DB_USERNAME=axspring
DB_PASSWORD=
DB_PORT=15432
REDIS_PORT=16379
REDIS_PASSWORD=
JWT_PRIVATE_KEY_PATH=secrets/jwt-private.pem
JWT_PUBLIC_KEY_PATH=secrets/jwt-public.pem
```

`.gitignore`:

```gitignore
.env
.env.*
!.env.example
```

### `compose.yml`

```yaml
services:
  postgres:
    image: postgres:17
    container_name: axspring-postgres

    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}

    ports:
      - "${DB_PORT}:5432"

    volumes:
      - axspring-postgres-data:/var/lib/postgresql/data

    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
      interval: 5s
      timeout: 3s
      retries: 10

volumes:
  axspring-postgres-data:
```

---

## 8. Makefile

반복적인 개발 명령은 Makefile로 관리합니다.

프로젝트 루트의 `.env`를 읽어 Docker Compose와 Spring Boot 양쪽에
환경변수를 전달합니다.

```makefile
.PHONY: up down restart logs db-shell run test clean migration

include .env
export

up:
    docker compose up -d

down:
    docker compose down

restart:
    docker compose down
    docker compose up -d

logs:
    docker compose logs -f

db-shell:
    docker exec -it axspring-postgres psql -U $(DB_USERNAME) -d $(DB_NAME)

run:
    ./gradlew bootRun

test:
    ./gradlew test

clean:
    ./gradlew clean

migration:
    @test -n "$(name)" || (echo "usage: make migration name=add_something" && exit 1)
    @version=$$(date +%Y%m%d%H%M%S); \
    file="src/main/resources/db/migration/V$${version}__$(name).sql"; \
    touch "$$file"; \
    echo "Created $$file"
```

주요 명령:

```bash
make up
make down
make restart
make logs
make db-shell
make run
make build
make test
make redis-cli
```

Migration 파일 생성 예:

```bash
make migration name=add_last_login_at
```

---

## 9. JPA / Flyway

Persistence 계층에는 다음을 사용합니다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

implementation 'org.flywaydb:flyway-core'
runtimeOnly 'org.flywaydb:flyway-database-postgresql'

runtimeOnly 'org.postgresql:postgresql'
```

로컬 설정 예:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false

  flyway:
    enabled: true
```

Schema 관리 책임:

```text
Flyway
→ DB Schema 생성/변경

Hibernate
→ Entity와 Schema 일치 여부 검증

ddl-auto=validate
→ Hibernate의 자동 Schema 변경 방지
```

`ddl-auto=update`에 의존하지 않습니다.

---

## 10. First Flyway Migration

위치:

```text
src/main/resources/db/migration/
└── V1__create_users_table.sql
```

현재 User schema 기준:

```sql
CREATE TABLE users (
    id                      VARCHAR(64)     PRIMARY KEY,

    name                    VARCHAR(100)    NOT NULL,
    email                   VARCHAR(320)    NOT NULL,
    email_verified_at       TIMESTAMPTZ,
    image_url               TEXT,

    password_hash           VARCHAR(255),
    role                    VARCHAR(32)     NOT NULL DEFAULT 'MEMBER',

    department              VARCHAR(100),
    position                VARCHAR(100),
    hire_date               DATE,
    birth_date              DATE,

    slack_user_id           VARCHAR(100),
    google_account_id       VARCHAR(255),
    notion_person_id        VARCHAR(255),
    linear_user_id          VARCHAR(255),
    github_login            VARCHAR(255),

    email_aliases           JSONB           NOT NULL DEFAULT '[]'::jsonb,

    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    must_change_password    BOOLEAN         NOT NULL DEFAULT TRUE,
    password_changed_at     TIMESTAMPTZ,

    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_users_role CHECK (
        role IN (
            'OWNER',
            'ADMIN',
            'MANAGER',
            'MEMBER',
            'VIEWER'
        )
    )
);

CREATE UNIQUE INDEX uk_users_email_lower
    ON users (LOWER(email));
```

### Migration Policy

Flyway migration 파일은 Git에 커밋합니다.

```text
Commit
├── V1__create_users_table.sql
├── V2__...
└── V3__...

Do not commit
└── .env
```

한 번 적용된 migration은 수정하지 않는 것을 원칙으로 합니다.

기존 스키마 변경이 필요하면 새로운 migration을 추가합니다.

```text
V1__create_users_table.sql
V2__add_last_login_at.sql
V3__add_external_identity_constraint.sql
```

---

## 11. Git Ignore Note

헥사고날 아키텍처에서 `application/port/out/` 패키지를 사용하기 때문에
`.gitignore`의 일반적인 `out/` 패턴과 충돌할 수 있습니다.

따라서:

```gitignore
# Bad
out/

# Good
/out/
```

처럼 프로젝트 루트의 build output만 무시하도록 설정합니다.

확인:

```bash
git check-ignore -v src/main/java/com/example/axspring/user/application/port/out/UserRepository.java
```

아무 출력도 없으면 해당 source file은 정상적으로 Git 추적 대상입니다.

---

## 12. Current Progress

현재 구현 흐름:

```text
Global HTTP Configuration
├── CORS
├── Request ID
├── Access Logging
├── Global Error Response
└── Validation Handling

User Domain
├── User
├── UserId
├── Email
└── UserRole

Register User
├── RegisterUserCommand
├── RegisterUserUseCase
├── RegisterUserService
├── UserRepository Port
└── PasswordEncoder Port
        ↓
BCryptPasswordEncoderAdapter

Authentication
├── LoginCommand / LoginResult / LoginUseCase
├── LoginService
├── UserCredentialRepository
├── AuthSessionRepository
├── SecureRefreshTokenGenerator
├── RedisAuthSessionRepositoryAdapter
└── JwtTokenAdapter

Infrastructure
├── Docker PostgreSQL
├── Docker Redis
├── .env
├── Makefile
├── JPA
└── Flyway
```

## Next Steps

다음 구현 순서:

```text
1. Register User Web Adapter / API
2. Login Web Adapter / API 및 Refresh Token 전달 방식 결정
3. Refresh Token 갱신·회전 Use Case
4. Access Token 검증 및 인증 필터
5. GET /api/users/me
6. 로그인·세션·인증 API 통합 테스트
```

Persistence 구현에서도 Domain `User`와 JPA `UserJpaEntity`는 분리합니다.

```text
User
→ Domain Model
→ JPA annotation 없음

UserJpaEntity
→ Persistence Model
→ @Entity 사용
```

이 원칙을 유지하면서 기능을 하나씩 확장합니다.

---

## 13. Authentication / Login

이메일과 비밀번호 기반 로그인을 Application Use Case로 구현했습니다.
아직 HTTP Controller는 없으며, 이후 Web Adapter가 `LoginCommand`를 만들고
`LoginUseCase`를 호출하는 구조로 연결할 예정입니다.

```text
LoginCommand
      ↓
LoginUseCase
      ↓
LoginService
      ↓
사용자 · 인증 정보 조회 및 비밀번호 검증
      ↓
AuthSession 생성 및 Redis 저장
      ↓
Access Token 발급
      ↓
LoginResult(accessToken, refreshToken)
```

주요 구성:

```text
auth/
├── application/
│   ├── port/in/
│   │   ├── LoginCommand.java
│   │   ├── LoginResult.java
│   │   └── LoginUseCase.java
│   ├── port/out/
│   │   ├── AuthSessionRepository.java
│   │   ├── RefreshTokenGenerator.java
│   │   └── TokenIssuer.java
│   └── service/
│       └── LoginService.java
├── domain/
│   └── AuthSession.java
└── adapter/out/
    ├── session/RedisAuthSessionRepositoryAdapter.java
    └── token/
        ├── JwtTokenAdapter.java
        └── SecureRefreshTokenGenerator.java
```

로그인 실패 시에는 이메일과 비밀번호 중 어느 값이 틀렸는지 구분하지 않고
`InvalidCredentialsException`을 사용합니다. 비활성 사용자는
`InactiveUserException`으로 처리합니다.

### Token Strategy

Access Token은 RSA 서명 JWT이며, 사용자 ID(`sub`), 세션 ID(`sid`), 역할
(`role`), issuer, audience, 만료 시간을 포함합니다.

Refresh Token은 JWT가 아닌 32바이트 `SecureRandom` 난수입니다. URL-safe
Base64 문자열로 응답에 한 번만 포함하고, 서버에는 BCrypt 해시만
`AuthSession`에 저장합니다. Redis 세션의 TTL은 Refresh Token 만료 시각과
같게 설정합니다.

```yaml
app:
  jwt:
    issuer: axspring-auth
    audience: axspring-api
    key-id: local-key-1
    private-key-path: ${JWT_PRIVATE_KEY_PATH}
    public-key-path: ${JWT_PUBLIC_KEY_PATH}
    access-token-ttl: 15m
    refresh-token-ttl: 14d
```

JWT 개인키와 공개키는 저장소에 커밋하지 않습니다. 예시 경로인 `secrets/`는
`.gitignore`에 포함되어 있습니다.

### Authentication Tests

`LoginServiceTest`는 정상 로그인에서 다음을 검증합니다.

- 비밀번호 검증 성공 후 Access/Refresh Token을 반환한다.
- Refresh Token 원문 대신 해시가 세션에 저장된다.
- 저장한 세션 ID로 Access Token을 발급한다.

전체 테스트 실행:

```bash
make test
```
