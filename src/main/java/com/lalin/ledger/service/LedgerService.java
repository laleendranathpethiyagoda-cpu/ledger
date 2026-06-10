package com.lalin.ledger.service;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import com.lalin.ledger.account.TransferType;
import com.lalin.ledger.dto.Transaction;
import com.lalin.ledger.exception.DuplicateTransactionException;
import com.lalin.ledger.exception.InsufficientFundsException;
import com.lalin.ledger.exception.AccountNotFoundException;
import com.lalin.ledger.repo.LedgerRepo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.stereotype.Service;



import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.lalin.ledger.repo.InitialData.tinyLedger;
import static java.lang.String.format;

@Service
@AllArgsConstructor
@Getter
public class LedgerService {

    private final LedgerRepo ledgerRepo;

    /**
     * Performs the withdrawal action
     * Update the balance in the account
     * Update the ledger entry for the said account
     * @param accountNumber customer account number
     * @param transactionId Idempotency Key
     * @param amount Withdrawal amount
     * @param description A description of the transaction
     */
    public void withdraw(AccountNumber accountNumber, UUID transactionId, @NotNull(message = "Amount is required") @DecimalMin(value = "1.00", message = "Amount must be at least GBO 1.00") BigDecimal amount, String description) {
        var tinyLedger = ledgerRepo.tinyLedger();
        checkForIdempotency(accountNumber.accountNumberValue(), tinyLedger, transactionId);
        Account account = getAccount(accountNumber);

        if(account.getBalance().compareTo(amount) < 0 )
            throw new InsufficientFundsException(format("Insufficient funds for withdrawal %s", transactionId));
        //, new MathContext(2, RoundingMode.HALF_UP
         ledgerRepo.getCache()
                .getAccounts()
                .computeIfPresent(accountNumber.accountNumberValue(), (key, ac) -> {
            ac.setBalance(ac.getBalance().subtract(amount));
            return ac;
        } );

        var entry = new LedgerEntry(transactionId, accountNumber, amount, TransferType.WITHDRAW, LocalDateTime.now(), description);
        updateLedger(accountNumber, tinyLedger, entry);
    }


    /**
     * Deposit flow
     * Update the balance
     * Add ledger entry
     * @param accountNumber
     * @param transactionId
     * @param amount
     * @param description
     */
    public void deposit(AccountNumber accountNumber, UUID transactionId, @NotNull(message = "Amount is required") @DecimalMin(value = "1.00", message = "Amount must be at least GBO 1.00") BigDecimal amount, String description) {;
        checkForIdempotency(accountNumber.accountNumberValue(), tinyLedger, transactionId);
        Account account = getAccount(accountNumber); // fail fast if account not present
        //, new MathContext(2, RoundingMode.HALF_UP
        ledgerRepo.getCache()
                .getAccounts()
                .computeIfPresent(accountNumber.accountNumberValue(), (acNumber, acc) -> {
                    acc.setBalance(acc.getBalance().add(amount));
                    return acc;
                } );

        var entry = new LedgerEntry(transactionId, accountNumber, amount, TransferType.DEPOSIT, LocalDateTime.now(), description);

        updateLedger(accountNumber, tinyLedger, entry);
    }

    private Account getAccount(AccountNumber accountNumber) {
        return ledgerRepo
                .getAccountById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
    }

    /**
     * Ledger Entry
     * Add a new entry if not present
     * or
     * Update existing list of ledger entries for a specific account
     * @param accountNumber
     * @param tinyLedger
     * @param entry
     */
    private static void updateLedger(AccountNumber accountNumber, ConcurrentHashMap<String, List<LedgerEntry>> tinyLedger, LedgerEntry entry) {
        tinyLedger.computeIfPresent(accountNumber.accountNumberValue(), (k, v ) -> {
            v.add(entry);
            return v;
        });
        //It could be the very first transaction record in the ledger for this account number
        tinyLedger.computeIfAbsent(accountNumber.accountNumberValue(), (k) -> {
            var list =   new ArrayList<LedgerEntry>();
            list.add(entry);
            return list;
        });
    }

    /**
     * Check for duplicate transactions
     * Each transaction carries with a (user-defined) unique idempotency key.
     * This is stored in the ledger entry and if a transaction is played more than once, it will be caught here.
     *
     * @param accountNumber
     * @param tinyLedger
     * @param transactionId
     */
    private void checkForIdempotency(String accountNumber, Map<String, List<LedgerEntry>> tinyLedger, UUID transactionId) {
        var ledgerEntries = tinyLedger.getOrDefault(accountNumber, List.of());
        if(ledgerEntries.stream().anyMatch(ledgerEntry -> ledgerEntry.getTransferId().compareTo(transactionId) == 0)) {
            throw new DuplicateTransactionException(format("Duplicate transaction %s", transactionId));
        }
    }

    /**
     * Return the account
     * @param accountNumber
     * @return Account instance
     */
    public Account balance(AccountNumber accountNumber) {
        return getAccount(accountNumber);
    }

    /**
     * Transaction history of an account - in REVERSE order. So latest at the top of the list
     * @param accountNumber
     * @return List<Transaction>
     */
    public List<Transaction> txHistory(AccountNumber accountNumber) {
        Account account =  getAccount(accountNumber);
        var txList = ledgerRepo.tinyLedger().get(account.getAccountNumber().accountNumberValue());
        return txList
                .stream()
                .sorted(Comparator.comparing(LedgerEntry::getTimestamp)
                        .reversed())
                .map(ledgerEntry ->
                new Transaction(ledgerEntry.getAccountNumber().accountNumberValue(),
                ledgerEntry.getAmount().toString(),
                        ledgerEntry.getTransferType(),
                        ledgerEntry.getTimestamp(),
                        ledgerEntry.getDescription()))
                .toList();

    }
}
