package com.lalin.ledger.account;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Account {

  private AccountNumber accountNumber;
  private int sortCode;
  private BigDecimal balance;

  public void deposit(BigDecimal amount) {
    balance.add(amount);
  }

  public void withdraw(BigDecimal amount) {
    balance.subtract(amount);
  }
}
