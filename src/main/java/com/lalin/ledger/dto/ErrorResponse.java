package com.lalin.ledger.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

  private Map<String, String> messages;
  private String error;
  private int status;
  private LocalDateTime timestamp;

}
