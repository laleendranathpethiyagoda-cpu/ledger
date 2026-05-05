package com.lalin.ledger.exception;

public class IncorrectTransferAmountException extends RuntimeException {
    public IncorrectTransferAmountException(String message) {
        super(message);
    }
}
