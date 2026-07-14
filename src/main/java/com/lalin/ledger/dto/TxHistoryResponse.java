package com.lalin.ledger.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TxHistoryResponse {

  private String accountNumber;
  private List<Transaction> txHistory;
  private LocalDateTime timestamp;
}
