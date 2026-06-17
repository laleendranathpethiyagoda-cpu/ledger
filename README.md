# Bank Ledger

## Introduction
This repo consists of te technical detail outlining
a bank ledger and how it manages transactions. 
In particular, the following features:

>- Ability to record money movements (ie: deposits and withdrawals)

>- View current balance

>- View transaction history

## The Chart of Accounts for this application is as follows:
Accounting Sequence

A chart of accounts (COA) lists all accounts in a company’s general ledger.
It helps organize financial data and supports financial reporting.
Each account typically has a number, name, and description.
Companies can customize their COA, but should keep it consistent over time.

>- Accounting entries are made to the general ledger
>- The general ledger feeds into the trial balance
>- The trial balance feeds into the financial statement.

## The following assumptions are made when implementing this solution:

>-Transfers (Withdrawals and Deposits) are immutable. They are never modified once they are successfully created.

>-Transfer is an immutable record of a financial transaction between two accounts.

>-There is at most one withdrawal or deposit with a particular id. This guarantees the idempotency of a transaction 
>
>- Double entry Book Keeping (Debit Bank - Credit Customer) aspects have not been implemented. The ledger is represented as a
Map, where the account number is the key, and the entries pertinent to that account number are held in a list structure. 
>- Account Name has been omitted for brevity.
>- All transactions are in GBP

## Set up and Configuration
>- The application is built using Spring boot. Usual configurations apply `spring.application.name=ledger
server.servlet.context-path=/ledger-tx`
>- The build tool used is Maven.
>- For ease of build, Maps (concurrentHashMap) are used to simulate the database layer, `to hold account and ledger details.`
>- All functionality can be accessed via REST Api calls.
>- Tests and the application can be invoked in the usual springboot style


> ## Implementation Details
>- The Account class represents a customer account and holds the current balance, amongst other things.
>- AccountNumber, of type record is used to create a special type to hold account numbers. This is to ensure type-safety during 
> transit
>- LedgerEntry represents an immutable entry in the ledger. Once added, these cannot be changed or deleted. Changes can and should be enacted via
> a reversal entry - not implemented here.
>- 
>- LedgerController is the main entry point to the functionality.
>- All mutations are protected by a trasnferID (Idempotency key)
>- An exception Handler (Rest Controller Advice) is used to respond to various exceptions. 
>- DTO pattern as well as the MVC pattern has been utilised in implementing this system.
>- Exceptions are used to manage failure scenarios.
>- Fields and Types
>  - TransferId : UUID.
>  - amount : BigDecimal.
>  - Account number : AccountNumber type: `Account numbers are 8 digits exactly. No leading zeros. Hyphens and spaces are removed. An example: 26082955.`
>  - TransferType : An enum (Withdraw/Deposit).
>  - <b>InitialData.java in `package com.lalin.ledger.repo;` has Account details for the reader's perusal.</B>
>  - 

### Tests
>- The service Layer (LedgerService.java) and the Controller (LedgerController.java) have been rigorously tested. 

## Running the Application

The application can be started from the main class `LedgerApplication.java` in `package com.lalin.ledger;`
<img alt="Screenshot 2026-05-04 at 23.07.49.png" src="../../../../var/folders/_l/pv4cf29907q7_cm1tkwyyvvw0000gn/T/TemporaryItems/NSIRD_screencaptureui_aGIR9k/Screenshot%202026-05-04%20at%2023.07.49.png"/>
## APIs

> ### Withdraw 
Rest end point :[POST] `http://localhost:<suitable-port>/ledger-tx/api/v1/accounts/{eight-digit-account-id}/withdraw`

Request:
```json
{
    "transferId": "019de8cb-c364-720d-98fd-8bc3e028c928",
    "amount": 10.69,
    "description": "Atm cash withdrawal"
}
```
Response:
```json
{
    "id": "019de8cb-c364-720d-98fd-8bc3e028c928",
    "amount": "10.69",
    "message": "Withdrawal success"
}
```

### Deposit
Rest end point :[POST] `http://localhost:<suitable-port>/ledger-tx/api/v1/accounts/{eight-digit-account-id}/deposit`

Request:
```json
{
    "transferId": "019de8cb-c364-720d-98fd-8bc3e028c934",
    "amount": 60.00,
    "description": "Pay in at bank"
}
```

Response:
```json
{
    "id": "019de8cb-c364-720d-98fd-8bc3e028c934",
    "amount": "60.00",
    "message": "Deposit Success"
}
```

### Balance
Rest end point :[GET] `http://localhost:<suitable-port>/ledger-tx/api/v1/accounts/{eight-digit-account-id}/balance`

Response:
```json
{
    "accountNumber": "26082955",
    "balance": "223.62",
    "timestamp": "2026-05-04T18:51:10.533861"
}
```

### History of the transactions : Provided in DESCENDING ORDER of `transactionDate`
Rest end point :[GET] `http://localhost:<suitable-port>/ledger-tx/api/v1/accounts/{eight-digit-account-id}/history`

Response:
```json
{
    "accountNumber": "26082955",
    "txHistory": [
        {
            "accountNumber": "26082955",
            "amount": "60.00",
            "type": "DEPOSIT",
            "transactionDate": "2026-05-04T18:51:02.818322",
            "description": "Pay in at bank"
        },
        {
            "accountNumber": "26082955",
            "amount": "50.00",
            "type": "DEPOSIT",
            "transactionDate": "2026-05-04T18:50:51.90437",
            "description": "Pay in at bank"
        },
        {
            "accountNumber": "26082955",
            "amount": "10.69",
            "type": "WITHDRAW",
            "transactionDate": "2026-05-04T18:50:40.01717",
            "description": "Atm cash withdrawal"
        },
        {
            "accountNumber": "26082955",
            "amount": "5.69",
            "type": "WITHDRAW",
            "transactionDate": "2026-05-04T18:50:28.727442",
            "description": "Atm cash withdrawal"
        },
        {
            "accountNumber": "26082955",
            "amount": "5.69",
            "type": "WITHDRAW",
            "transactionDate": "2026-05-04T18:50:21.605602",
            "description": "Atm cash withdrawal"
        }
    ],
    "timestamp": "2026-05-04T18:51:21.076813"
}

```