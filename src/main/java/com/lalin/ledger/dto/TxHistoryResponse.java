package com.lalin.ledger.dto;

import com.lalin.ledger.account.LedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TxHistoryResponse {
    private String accountNumber;
    private List<Transaction> txHistory;
    private LocalDateTime timestamp;
}
