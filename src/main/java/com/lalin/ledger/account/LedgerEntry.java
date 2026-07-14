package com.lalin.ledger.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LedgerEntry {

  private UUID transferId;
  private AccountNumber accountNumber;
  private BigDecimal amount;
  private TransferType transferType;
  private LocalDateTime timestamp;
  private String description;
}
