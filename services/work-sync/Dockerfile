FROM golang:1.25-alpine AS builder

WORKDIR /app

COPY go.mod ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build \
    -o /work-sync \
    ./cmd/worker


FROM alpine:3.21

WORKDIR /app

COPY --from=builder /work-sync /usr/local/bin/work-sync

ENTRYPOINT ["work-sync"]
