package com.lalin.ledger.repo;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@AllArgsConstructor
public final class InitialData {

  public static ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<String, List<LedgerEntry>> tinyLedger = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<String, List<LedgerEntry>> tempLedger = new ConcurrentHashMap<>();

  {
    var accountA = new Account(new AccountNumber("26082955"), 123456, BigDecimal.valueOf(135.69));
    var accountB = new Account(new AccountNumber("26082956"), 123456, BigDecimal.valueOf(135.70));
    var accountC = new Account(new AccountNumber("26082957"), 123456, BigDecimal.valueOf(135.71));

    accounts.putIfAbsent(accountA.getAccountNumber().accountNumberValue(), accountA);
    accounts.putIfAbsent(accountB.getAccountNumber().accountNumberValue(), accountB);
    accounts.putIfAbsent(accountC.getAccountNumber().accountNumberValue(), accountC);
  }

  public ConcurrentHashMap<String, Account> getAccounts() {
    return accounts;
  }

  public ConcurrentHashMap<String, List<LedgerEntry>> getTinyLedger() {
    return tinyLedger;
  }

  public ConcurrentHashMap<String, List<LedgerEntry>> getTempLedger() {
    return tempLedger;
  }

}

