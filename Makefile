.PHONY: up down restart logs db-shell run test clean

include .env
export

# CI/샌드박스 환경에서도 사용자 홈 디렉터리 권한에 의존하지 않도록,
# Gradle 캐시를 프로젝트 내부의 무시된 .gradle 디렉터리에 둡니다.
export GRADLE_USER_HOME ?= $(CURDIR)/.gradle

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

build: 
	./gradlew build

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

redis-cli:
	docker exec -it axspring-redis redis-cli -a $(REDIS_PASSWORD)
