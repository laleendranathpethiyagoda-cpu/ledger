package com.lalin.ledger.repo;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.Optional.ofNullable;

@Repository
@AllArgsConstructor
@Getter
@Setter
public class LedgerRepo {

    private final InitialData cache;

    public Optional<Account> getAccountById(AccountNumber accountNumber) {
        return ofNullable(cache.getAccounts().get(accountNumber.accountNumberValue()));
    }

    public ConcurrentHashMap<String, List<LedgerEntry> >tinyLedger() {
        return cache.getTinyLedger();
    }

}


