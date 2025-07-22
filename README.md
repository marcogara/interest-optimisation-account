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

-- Optional: Allow your user access to the table and sequences
GRANT ALL PRIVILEGES ON TABLE users TO store_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO store_user;
```

# interest-optimisation-account