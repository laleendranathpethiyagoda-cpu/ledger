-------------------------
--ASSETS:  ASSETS
-------------------------
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10100', 'ASSETS', 'Asset'::account_type, 'Cash In Bank', false, '10100',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10100-100', 'CASH', 'Asset'::account_type, 'CASH IN HAND', true, '10100',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10100-200', 'Balances with CB', 'Asset'::account_type, 'CB Balance', true, '10100',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
-------------------------
--ASSETS : LOANS AND ADVANCES
-------------------------
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10200', 'Loans and Advances', 'Asset'::account_type, 'Loans and Advances', false, '10200',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10200-100', 'Mortgages', 'Asset'::account_type, 'Mortgages', true, '10200',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10200-200', 'Personal Loans', 'Asset'::account_type, 'Personal Loans', true, '10200',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10200-300', 'Commercial Loans', 'Asset'::account_type, 'Commercial Loans', true, '10200',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf,
                               parentcode, reportingtype)
VALUES ('10200-400', 'CreditCard Receivables', 'Asset'::account_type, 'CC REC', true, '10200',
        'BALANCE_SHEET'::reporting_type);
COMMIT;
