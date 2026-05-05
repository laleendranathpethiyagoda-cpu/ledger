package com.lalin.ledger.exception.handler;

import com.lalin.ledger.dto.ErrorResponse;
import com.lalin.ledger.exception.AccountNotFoundException;
import com.lalin.ledger.exception.DuplicateTransactionException;
import com.lalin.ledger.exception.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@RestControllerAdvice
public class LedgerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgExceptions(
            IllegalArgumentException ex) {

        Map<String, String> error = new HashMap<>();
       error.put("error", ex.getMessage());
        var response = new ErrorResponse();
        response.setError("Transfer Id error");
        response.setMessages(error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        var response = new ErrorResponse();
        response.setError(format("Payload failed to validate: %s " , errors));
        response.setMessages(errors);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

   @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(
           AccountNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        var response = new ErrorResponse();
        response.setError(format("Account error: %s " , ex.getMessage()));
        response.setMessages(error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(
            InsufficientFundsException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        var response = new ErrorResponse();
        response.setError(format("Funds error: %s " , ex.getMessage()));
        response.setMessages(error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTransactionException(
            DuplicateTransactionException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMsg());
        var response = new ErrorResponse();
        response.setError(format("Transaction error: %s " , ex.getMsg()));
        response.setMessages(error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
