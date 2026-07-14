package com.lalin.ledger.repo;

import static java.util.Optional.ofNullable;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@Getter
@Setter
public class LedgerRepo {

  private final InitialData cache;

  public Optional<Account> getAccountById(AccountNumber accountNumber) {
    return ofNullable(cache.getAccounts().get(accountNumber.accountNumberValue()));
  }

  public Map<String, List<LedgerEntry>> accountTransferSession() {
    return cache.getTempLedger();
  }

  public ConcurrentHashMap<String, List<LedgerEntry>> tinyLedger() {
    return cache.getTinyLedger();
  }

}


