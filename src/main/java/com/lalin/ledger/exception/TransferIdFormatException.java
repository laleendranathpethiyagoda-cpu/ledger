package com.lalin.ledger.exception;

public class TransferIdFormatException extends RuntimeException {

  private String message;
  private Exception exception;

  public TransferIdFormatException(String message, IllegalArgumentException exception) {
    this.message = message;
    this.exception = exception;

  }

  public String errorMessage() {
    return "Error : " + message + " " + exception.getLocalizedMessage();
  }
}
