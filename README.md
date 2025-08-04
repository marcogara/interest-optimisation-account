## 🗄️ Database Setup (PostgreSQL)

This project uses **PostgreSQL** as its database, with a `users` table to store registered users (`name`, `email`, `password`).

### ✅ Requirements

- PostgreSQL installed locally (recommended: version 13+)
- A PostgreSQL user and database set up
- Spring Boot will connect to this database on startup

---

### 🔧 1. Create the Database and User

Log into PostgreSQL (as a superuser):

```bash
psql -U postgres
```
### 🔧 2. Then run the following SQL commands:

```bash
CREATE DATABASE storedb;

CREATE USER store_user WITH PASSWORD 'securepassword';

GRANT ALL PRIVILEGES ON DATABASE storedb TO store_user;

\c storedb

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
  
ALTER TABLE users
ADD COLUMN interest DOUBLE PRECISION DEFAULT 0,
ADD COLUMN account DOUBLE PRECISION DEFAULT 0;
ADD COLUMN pending_interest_monthly_payment DOUBLE PRECISION DEFAULT 0;
ALTER TABLE users ADD COLUMN last_pending_interest_update DATE;

CREATE TABLE bank (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    interest DOUBLE PRECISION NOT NULL,
    account DOUBLE PRECISION NOT NULL
);

-- Optional: Allow your user access to the table and sequences
GRANT ALL PRIVILEGES ON TABLE users TO store_user;
GRANT ALL PRIVILEGES ON TABLE bank TO store_user;
GRANT USAGE, SELECT ON SEQUENCE bank_id_seq TO store_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO store_user;
```
```bash
+------------------------+           +-------------------------+
|        bank            |<----------|     bank_allocation     |
|------------------------|    FK     |-------------------------|
| id (PK)                |           | id (PK)                 |
| name                   |           | bank_id (FK -> bank.id) |
| interest               |           | amount                  |
| account                |           | source                  |
+------------------------+           | status                  |
                                     | timestamp               |
                                     +-------------------------+

+-------------------------+
|    interest_snapshot    |
|-------------------------|
| id (PK)                 |
| effective_interest      |
| valid_from              |
| valid_to                |
+-------------------------+
```
# interest-optimisation-account

```bash
CREATE TABLE bank_allocation (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),       
    bank_id INTEGER NOT NULL REFERENCES bank(id),
    amount DOUBLE PRECISION NOT NULL CHECK (amount >= 0),
    source VARCHAR(50) NOT NULL,                           -- e.g., 'deposit', 'rebalancing'
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',          -- e.g., 'ACTIVE', 'REALLOCATED'
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

  
CREATE TABLE interest_snapshot (
    id SERIAL PRIMARY KEY,
    effective_interest DOUBLE PRECISION NOT NULL CHECK (effective_interest >= 0),
    valid_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_to TIMESTAMP  -- NULL means the snapshot is currently active
);

GRANT ALL PRIVILEGES ON TABLE bank_allocation TO store_user;
GRANT ALL PRIVILEGES ON TABLE interest_snapshot TO store_user;
GRANT USAGE, SELECT ON SEQUENCE bank_allocation_id_seq TO store_user;
GRANT USAGE, SELECT ON SEQUENCE interest_snapshot_id_seq TO store_user;

```
