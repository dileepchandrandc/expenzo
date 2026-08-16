-- Test schema for H2 (PostgreSQL mode) — expenzo-payment-card-service

-- Enum used by BankAccount.account_type (referenced via BankAccountRepository)
CREATE TYPE account_type_enum AS ENUM ('SAVINGS', 'CURRENT');

CREATE TABLE expenzo_user (
    id            VARCHAR(36) NOT NULL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    country_code  VARCHAR(5),
    mobile_number VARCHAR(20),
    password      VARCHAR(255)
);

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

CREATE TABLE credit_card (
    id              VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id         VARCHAR(36)     NOT NULL,
    bank_account_id VARCHAR(36),
    card_number     VARCHAR(19)     NOT NULL,
    valid_from      DATE            NOT NULL,
    valid_to        DATE            NOT NULL,
    credit_limit    NUMERIC(12, 2)  NOT NULL,
    billing_date    SMALLINT        NOT NULL,
    nick_name       VARCHAR(50),
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    is_active       BOOLEAN         NOT NULL,
    deleted_at      TIMESTAMP
);

ALTER TABLE credit_card ADD CONSTRAINT uq_credit_card_number UNIQUE (card_number);

CREATE TABLE debit_card (
    id              VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id         VARCHAR(36) NOT NULL,
    bank_account_id VARCHAR(36) NOT NULL,
    card_number     VARCHAR(19) NOT NULL,
    valid_from      DATE        NOT NULL,
    valid_to        DATE        NOT NULL,
    nick_name       VARCHAR(50),
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP   NOT NULL,
    is_active       BOOLEAN     NOT NULL,
    deleted_at      TIMESTAMP
);

ALTER TABLE debit_card ADD CONSTRAINT uq_debit_card_number UNIQUE (card_number);
