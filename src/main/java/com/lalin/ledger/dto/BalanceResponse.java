package com.lalin.ledger.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BalanceResponse {

  private String accountNumber;
  private String balance;
  private LocalDateTime timestamp;
}
