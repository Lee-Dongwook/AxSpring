# Work Sync Service

External work ingestion service for:

- Notion
- Linear
- GitHub

Responsibilities:

- incremental sync
- cursor management
- distributed locking
- retry/backoff
- normalization
- publishing normalized work items to Redis Streams

This service does not own Task persistence.
Spring backend remains the source of truth for Task domain data.
