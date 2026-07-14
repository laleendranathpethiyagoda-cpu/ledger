package com.lalin.ledger.exception;

import lombok.Getter;

@Getter
public class DuplicateTransactionException extends RuntimeException {

  private final String msg;

  public DuplicateTransactionException(String message) {
    this.msg = message;
  }

}
