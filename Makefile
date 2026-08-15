.PHONY: up down restart logs db-shell run test clean

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

build: 
	./gradlew build

test:
	./gradlew test

clean:
	./gradlew clean
