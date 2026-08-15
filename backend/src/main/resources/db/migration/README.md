# Database Migration Scripts

This folder contains the raw `.sql` files generated from the `POSTGRESQL_SCHEMA.md` specification.

- `V1__initial_schema.sql`: Contains all `CREATE EXTENSION`, `CREATE TYPE`, and `CREATE TABLE` definitions, along with constraints and indexing.
- `V2__seed_reference_data.sql`: Contains the `INSERT` statements needed to seed the required Reference / System tables.

These scripts are written in a dependency-safe order and are designed to execute seamlessly with Flyway or Liquibase in a local or production PostgreSQL instance.
