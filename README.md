# BlockVote — Decentralized Voting System

## What problem it solves

Every electronic voting system runs into the same trust problem: the votes live in a database, and whoever administers that database can quietly change or delete a row afterwards with nothing left to show for it. Auditing after the fact means taking the operator's word for it. BlockVote removes that possibility by writing every vote twice — once into a normal table for tallying, and once into an append-only ledger where each entry stores a SHA-256 hash of its own contents plus the hash of the entry before it. Because each block is chained to its predecessor, editing or deleting any past vote invalidates the hash of every block after it, and `GET /api/ledger/validate` walks the whole chain and reports the exact block ID where it broke. Voter identity stays separated from the count: registration and login are JWT-based, a voter is allowed one vote per election, and results are served from aggregated totals while the ledger remains independently verifiable by anyone with read access.

## Tech stack

Spring Boot 3 (Java 24) · Spring Security + JJWT · Spring Data JPA · PostgreSQL 15 · Docker Compose · Maven wrapper

## API overview

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/auth/register` | Create a voter or admin account |
| `POST` | `/api/auth/login` | Exchange credentials for a JWT |
| `GET` | `/api/auth/whoami` | Identity of the current token holder |
| `POST` | `/api/election` | Create an election (admin) |
| `GET` | `/api/election` | List all elections |
| `POST` | `/api/candidates` | Add a candidate to an election (admin) |
| `GET` | `/api/candidates/election/{id}` | Candidates standing in an election |
| `POST` | `/api/votes` | Cast a vote — also appends a ledger block |
| `GET` | `/api/votes/election/{electionId}` | Votes recorded in an election |
| `GET` | `/api/votes/results/{electionId}` | Tally for an election |
| `GET` | `/api/ledger` | The full hash-chained ledger |
| `GET` | `/api/ledger/validate` | Verify the chain end to end |

Send the JWT as `Authorization: Bearer <token>` on everything except register and login.

## How to run it

### With Docker (easiest)

Brings up PostgreSQL and the API together; the app waits for the database health check before starting.

```bash
git clone https://github.com/SujalPati1/Decentralized-Voting-System.git
cd Decentralized-Voting-System
docker compose up --build
```

The API is then on `http://localhost:8080` and Postgres on `5432`. Before running this anywhere but your own machine, change the `POSTGRES_PASSWORD` in `docker-compose.yml` — it is currently a hardcoded development password.

### Locally, without Docker

Needs JDK 24 and a PostgreSQL instance you've already created a `blockvote_db` database in. Every setting is read from the environment, so export them first:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/blockvote_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=yourpassword
export SPRING_JPA_HIBERNATE_DDL_AUTO=update
export JWT_SECRET=a-long-random-base64-secret
export JWT_EXPIRATION=86400000

./mvnw spring-boot:run          # use mvnw.cmd on Windows
```

Hibernate creates the schema on first boot. Run the tests with `./mvnw test`, or build a jar with `./mvnw clean package` and start it via `java -jar target/blockvote-0.0.1-SNAPSHOT.jar`.

### Quick smoke test

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"voter@example.com","password":"secret123",
       "fullName":"Test Voter","voterId":"TESTVOTER001","aadharOrPan":"PLACEHOLDER"}'

curl http://localhost:8080/api/ledger/validate
# → "Ledger chain is valid"
```
