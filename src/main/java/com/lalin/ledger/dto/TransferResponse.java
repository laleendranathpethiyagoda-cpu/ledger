package com.lalin.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransferResponse {

  private String id;
  //@NotNull(message = "Amount is required")
  // @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
  private String amount;
  private String message;
}
