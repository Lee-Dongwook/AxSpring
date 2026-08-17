# axspring Backend

사내 업무 서비스를 위한 Spring Boot 백엔드 MVP입니다. 헥사고날 아키텍처를 적용해 HTTP, 애플리케이션 유스케이스, 도메인, DB·Redis 어댑터의 책임을 분리했습니다.

## 현재 구현 범위

- 사용자 회원가입: `POST /api/users`
- 이메일/비밀번호 로그인: `POST /api/auth/login`
  - Access Token(RSA JWT) 발급
  - Refresh Token은 HttpOnly Cookie로 전달하고 Redis에는 해시만 저장
- OCR API: 명함·영수증 이미지 업로드
  - JWT 인증, 이미지 검증, OCR Provider Port, Automation Run·감사 로그 기록을 지원
  - 기본값은 Mock OCR이며, Claude 어댑터는 실제 Messages API 연결 전 단계입니다.
- 공통 기능: CORS, Request ID, 접근 로그, 입력값 검증, 공통 오류 응답
- 인프라: PostgreSQL(Flyway migration), Redis, Prometheus, Grafana, Go 기반 work-sync worker

## 기술 스택

- Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA/Redis
- PostgreSQL 17, Flyway, Docker Compose
- JUnit 5, Mockito, Spring Security Test

## 구조

```text
Web Adapter → Inbound Port → Application Service → Domain → Outbound Port → DB / Redis / 외부 서비스
```

주요 소스는 `src/main/java/com/example/axspring` 아래에 있으며, `user`, `auth`, `ocr`, `global` 패키지로 나뉩니다.

## 실행 준비

1. `.env.example`을 복사해 `.env`를 만들고 값(특히 DB/Redis/Grafana 비밀번호)을 채웁니다. `.env`와 JWT 키 파일은 커밋하지 않습니다.
2. RSA 키를 생성합니다.

```bash
mkdir -p secrets
openssl genpkey -algorithm RSA -out secrets/jwt-private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in secrets/jwt-private.pem -out secrets/jwt-public.pem
```

3. 인프라를 실행한 뒤 애플리케이션을 시작합니다.

```bash
make up
make run
```

기본 포트는 PostgreSQL `15432`, Redis `16379`, 애플리케이션 `8080`입니다. `local` 프로필이 기본 적용되어 OCR Mock 구현체가 활성화됩니다.

### OCR 환경 변수

기본값은 `OCR_PROVIDER=mock`입니다. 이 값으로 명함·영수증 OCR API를 외부 API 비용 없이 확인할 수 있습니다.

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `OCR_PROVIDER` | `mock` | `mock` 또는 추후 실제 연결 시 `claude` |
| `ANTHROPIC_API_KEY` | - | Claude 사용 시에만 설정하는 API 키 |
| `OCR_CLAUDE_MODEL` | `claude-sonnet-5` | Claude OCR 모델 이름 |

현재 Claude 어댑터는 요청 재시도·Circuit Breaker와 응답 DTO 구조만 준비되어 있습니다. `OCR_PROVIDER=claude`는 Anthropic Messages API 연결 구현을 완료한 뒤에 사용하세요.

## 주요 API

| 기능       | 요청                                                          |
| ---------- | ------------------------------------------------------------- |
| 상태 확인  | `GET /health`                                                 |
| 회원가입   | `POST /api/users`                                             |
| 로그인     | `POST /api/auth/login`                                        |
| 명함 OCR   | `POST /api/ocr/business-card` (`multipart/form-data`, `file`) |
| 영수증 OCR | `POST /api/ocr/receipt` (`multipart/form-data`, `file`)       |
| API 문서   | `GET /swagger-ui/index.html`                                  |

OCR 요청은 Bearer JWT가 필요하며 `image/jpeg`, `image/png`, `image/webp` 파일만 업로드할 수 있습니다. 파일은 최대 5MB입니다.

```bash
curl -X POST http://localhost:8080/api/ocr/receipt \
  -H "Authorization: Bearer <access_token>" \
  -F "file=@receipt.png;type=image/png"
```

성공하면 OCR 결과를 반환하고, `automation_runs`에는 `RUNNING → SUCCESS` 상태와 결과를, `audit_logs`에는 OCR 수행 감사 로그를 기록합니다. Provider 호출 실패 시에는 Automation Run이 `FAILED`로 기록됩니다.

| 상황 | 응답 |
| --- | --- |
| JWT 없음 | `401 Unauthorized` |
| 허용하지 않은 MIME 타입 | `400 Bad Request` |
| 5MB 초과 파일 | `413 Payload Too Large` |

회원가입 예시:

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"홍길동","email":"hong@example.com","password":"password123"}'
```

## 테스트

```bash
make test
# 또는
./gradlew test
```

현재 도메인·회원가입·로그인 서비스 단위 테스트와 OCR MVC smoke test가 포함되어 있습니다. OCR smoke test는 JWT 인증, 파일 검증, Mock 결과, Automation Run 상태 전이, Audit Log 기록, provider 실패 기록을 검증합니다.

## 이번 점검에서 보완한 사항

- 애플리케이션 설정에 PostgreSQL, Redis, JWT 설정을 연결했습니다.
- JPA 엔티티와 스키마가 일치하도록 `user_credentials` Flyway migration을 추가했습니다.
- OCR 1차 흐름을 구현했습니다: JWT 인증 → Multipart 검증 → Provider Port → Mock/Claude Adapter → Automation Run → Audit Log → 오류 응답 → API 응답
- OCR 파일 타입 오류는 400, 크기 초과는 413으로 응답하도록 처리했습니다.
- 감사 로그 테이블 Flyway migration과 OCR 성공 감사 로그 기록을 추가했습니다.
- work-sync 스텁이 즉시 종료되지 않고 종료 신호를 기다리도록 수정했습니다.

## 다음 작업

- Refresh Token 갱신·회전 API
- `GET /api/users/me` 및 JWT 인증 통합 테스트
- Claude Messages API 실제 호출 구현 및 운영 환경 설정 분리
