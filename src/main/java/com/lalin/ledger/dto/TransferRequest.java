package com.lalin.ledger.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransferRequest {

  @NotNull(message = "Idempotency Key is required")
  private String transferId;
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
  private BigDecimal amount;
  private String description;
}
