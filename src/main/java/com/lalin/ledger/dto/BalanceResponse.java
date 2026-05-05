package com.lalin.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class BalanceResponse {
    private String accountNumber;
    private String balance;
    private LocalDateTime timestamp;
}
