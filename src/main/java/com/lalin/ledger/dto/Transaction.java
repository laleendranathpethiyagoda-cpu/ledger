package com.lalin.ledger.dto;

import com.lalin.ledger.account.TransferType;
import java.time.LocalDateTime;

public record Transaction(String accountNumber, String amount, TransferType type,
                          LocalDateTime transactionDate, String description) {

}
