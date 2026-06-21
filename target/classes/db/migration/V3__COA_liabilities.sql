
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100', 'LIABILITIES', 'Liability'::account_type, 'LIABILITIES',false, '20100','BALANCE_SHEET'::reporting_type);
COMMIT;

-------------------------
--LIABILITIES: CUSTOMER DEPOSITS
-------------------------
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100-01', 'Customer Deposits', 'Liability'::account_type, 'LIABILITIES: Customer Deposits',false, '20100','BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100-01-100', 'Savings Accounts', 'Liability'::account_type, 'DEPOSITS: Savings Accounts',true, '20100-01','BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100-01-200', 'Current Accounts', 'Liability'::account_type, 'Current Accounts',true, '20100-01','BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100-01-300', 'Term Deposits', 'Liability'::account_type, 'Term Deposits',true, '20100-01','BALANCE_SHEET'::reporting_type);
COMMIT;
-------------------------
--LIABILITIES: Borrowings
-------------------------
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('20100-02', 'Borrowings', 'Liability'::account_type, 'LIABILITIES: Customer Deposits',false, '20100','BALANCE_SHEET'::reporting_type);
COMMIT;
INSERT INTO chart_of_accounts (accountcode, accountname, accounttype, description, isleaf, parentcode, reportingtype)
VALUES ('10200-02-100', 'Interbank Loans', 'Liability'::account_type, 'Interbank loans',true, '20100-02','BALANCE_SHEET'::reporting_type);
COMMIT;
