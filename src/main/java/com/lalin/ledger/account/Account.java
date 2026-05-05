package com.lalin.ledger.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class Account {
    private AccountNumber accountNumber;
    private int sortCode;
    private BigDecimal balance;

}
