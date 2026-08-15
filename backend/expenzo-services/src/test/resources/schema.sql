-- Test schema for H2 (PostgreSQL mode)
-- Mirrors the production Postgres schema needed by the JPA entities.

-- Enum used by BankAccount.account_type
CREATE TYPE account_type_enum AS ENUM ('SAVINGS', 'CURRENT');

CREATE TABLE expenzo_user (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255),
    country_code  VARCHAR(5),
    mobile_number VARCHAR(20),
    password      VARCHAR(255) NOT NULL
);

ALTER TABLE expenzo_user ADD CONSTRAINT uq_expenzo_user_email UNIQUE (email);

CREATE TABLE bank (
    id         VARCHAR(36)  NOT NULL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    short_name VARCHAR(25)  NOT NULL
);

ALTER TABLE bank ADD CONSTRAINT uq_bank_name UNIQUE (name);
ALTER TABLE bank ADD CONSTRAINT uq_bank_short_name UNIQUE (short_name);

CREATE TABLE bank_account (
    id             VARCHAR(36)       NOT NULL PRIMARY KEY,
    user_id        VARCHAR(36)       NOT NULL,
    bank_id        VARCHAR(36)       NOT NULL,
    account_type   account_type_enum NOT NULL,
    account_number VARCHAR(50)       NOT NULL,
    nick_name      VARCHAR(50),
    created_at     TIMESTAMP         NOT NULL,
    updated_at     TIMESTAMP         NOT NULL,
    is_active      BOOLEAN           NOT NULL,
    deleted_at     TIMESTAMP
);

ALTER TABLE bank_account ADD CONSTRAINT uq_bank_account_number UNIQUE (account_number);
