-- Test schema for H2 (PostgreSQL mode) — expenzo-user-account-service
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
