# Library Domain Example

This repository is a modular Spring application for a public library.

The application contains two bounded contexts:

- `catalogue` for books and physical book instances;
- `lending` for patrons, holds, checkouts, overdue work, daily sheets, and patron profiles.

Domain events connect lending aggregates with persistence adapters and read models.
The production source is under `src/main/java`. Unit tests and database-backed
integration tests are under `src/test/groovy` and `src/integration-test/groovy`.

## Build

The project uses Maven and Java 11. The Maven wrapper is included:

```bash
./mvnw test
```

The original design notes are kept in `docs/project-architecture-notes.md` for
historical reference.
