CREATE TYPE reporting_type AS ENUM ('BALANCE_SHEET', 'PROFIT_LOSS');
CREATE TYPE account_type AS ENUM ('Asset', 'Liability', 'Equity', 'Income', 'Expense');
CREATE TYPE account_status AS ENUM ('Active', 'Dormant', 'Closed', 'Blocked', 'Frozen');
CREATE TYPE cat_type AS ENUM (
    'SALARY',
    'OTHER_INCOME',
    'TRANSFER_IN',
    'TRANSFER_OUT',
    'DEPOSIT',
    'WITHDRAWAL',
    'BONUS',
    'LOAN',
    'INTEREST',
    'OTHER_EXPENSE'
    );

CREATE TABLE IF NOT EXISTS chart_of_accounts
(
    accountCode   VARCHAR(50) PRIMARY KEY,
    accountName   VARCHAR(50)  NOT NULL UNIQUE,
    accountType   account_type NOT NULL,
    description   VARCHAR(255),
    isLeaf        BOOLEAN      NOT NULL,
    parentCode    VARCHAR(50) REFERENCES chart_of_accounts (accountCode),
    reportingType reporting_type,
    createdAt     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt     TIMESTAMP,

    CONSTRAINT chk_leaf_posting CHECK (
        (isLeaf = TRUE) OR (isLeaf = FALSE)
        )
);

CREATE TABLE IF NOT EXISTS customer
(
    customerId           UUID               DEFAULT gen_random_uuid() PRIMARY KEY,
    firstName            TEXT      NOT NULL,
    lastName             TEXT      NOT NULL,
    email                TEXT      NOT NULL,
    primaryPhoneNumber   TEXT      NOT NULL,
    secondaryPhoneNumber TEXT,
    houseNumberOrName    TEXT      NOT NULL,
    town                 TEXT      NOT NULL,
    dateOfBirth          DATE      NOT NULL,
    streetName           TEXT      NOT NULL,
    townOrCity           TEXT      NOT NULL,
    county               TEXT      NOT NULL,
    postcode             TEXT      NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts
(
    accountId     UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    accountNumber INTEGER        NOT NULL UNIQUE,
    sortingCode   INTEGER        NOT NULL,
    iban          TEXT           NOT NULL,
    accountName   VARCHAR(255)   NOT NULL,
    parentCode    VARCHAR(50) REFERENCES chart_of_accounts (accountCode),
    balance       NUMERIC(10, 2) NOT NULL,
    accountStatus account_status NOT NULL,
    createdAt     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt     TIMESTAMP,
    customerId    UUID REFERENCES customer (customerId) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS loan_account
(
    accountId     UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    accountNumber INTEGER        NOT NULL UNIQUE,
    sortingCode   INTEGER        NOT NULL,
    accountName   VARCHAR(255)   NOT NULL,
    balance       NUMERIC(10, 2) NOT NULL,
    accountStatus account_status NOT NULL,
    createdAt     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt     TIMESTAMP,
    customerId    UUID REFERENCES customer (customerId) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction_category
(
    categoryCode VARCHAR(50) PRIMARY KEY,
    categoryName VARCHAR(50) NOT NULL UNIQUE,
    description  VARCHAR(255),
    categoryType cat_type    NOT NULL,
    createdAt    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions
(
    transactionId           UUID PRIMARY KEY,
    accountId               UUID REFERENCES accounts (accountId),
    transactionType         VARCHAR(20),
    amount                  DECIMAL(18, 2) NOT NULL,
    currency                CHAR(3)        NOT NULL  DEFAULT 'GBP',
    referenceId             VARCHAR(50),
    transactionCode         VARCHAR(10),
    description             TEXT,
    bookingDate             TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    valueDate               DATE           NOT NULL,
    balanceAfterTransaction DECIMAL(18, 2)
);

CREATE TABLE IF NOT EXISTS gl_postings
(
    postingId     UUID PRIMARY KEY,
    accountCode   VARCHAR(10) REFERENCES chart_of_accounts (accountCode),
    transactionId UUID REFERENCES transactions (transactionId),
    debitAmount   DECIMAL(18, 2),
    creditAmount  DECIMAL(18, 2),
    postedAt      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION fn_enforce_leaf_node()
    RETURNS TRIGGER AS
$function$
BEGIN
    IF NOT EXISTS (SELECT 1
                   FROM chart_of_accounts
                   WHERE accountCode = NEW.accountCode
                     AND isLeaf = TRUE) THEN
        RAISE EXCEPTION 'Database Violation: Cannot assign a financial balance to a non-leaf Parent Account (%)', NEW.accountCode;
    END IF;

    RETURN NEW;
END;
$function$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_enforce_gl_leaf ON gl_postings;
DROP TRIGGER IF EXISTS trg_enforce_gl_leaf ON accounts;

CREATE TRIGGER trg_enforce_gl_leaf
    BEFORE INSERT OR UPDATE
    ON gl_postings
    FOR EACH ROW
EXECUTE FUNCTION fn_enforce_leaf_node();

CREATE TRIGGER trg_enforce_accounts_leaf
    BEFORE INSERT OR UPDATE
    ON accounts
    FOR EACH ROW
EXECUTE FUNCTION fn_enforce_leaf_node();