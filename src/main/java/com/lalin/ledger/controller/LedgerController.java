package com.lalin.ledger.controller;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import com.lalin.ledger.dto.*;
import com.lalin.ledger.exception.TransferIdFormatException;
import com.lalin.ledger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The Controller for ledger and accounts-based operations
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
@Validated
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransferResponse> deposit(
            @PathVariable String id,
            @Valid @RequestBody TransferRequest request) {
        var trfId = validateTransferId(request.getTransferId());
        var accountNumber = new AccountNumber(id);
        ledgerService.deposit(accountNumber, trfId, request.getAmount(), request.getDescription());
        var response = new TransferResponse(request.getTransferId(),
                request.getAmount().toString(),
                "Deposit Success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransferResponse> withdraw(
            @PathVariable String id,
            @Valid @RequestBody TransferRequest request) {
        var trfId = validateTransferId(request.getTransferId());
        var accountNumber = new AccountNumber(id);
        ledgerService.withdraw(accountNumber, trfId, request.getAmount(), request.getDescription());
        var response = new TransferResponse(request.getTransferId(),
                request.getAmount().toString(),
                "Withdrawal success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> balance(@PathVariable String id) {
        var acc = new AccountNumber(id);
        Account account = ledgerService.balance(acc);
        var response = new BalanceResponse(account.getAccountNumber().accountNumberValue(), account.getBalance().toString(), LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<TxHistoryResponse> txHistory(@PathVariable String id) {
        var acc = new AccountNumber(id);
        List<Transaction> transactions = ledgerService.txHistory(acc);
       var response = new TxHistoryResponse(acc.accountNumberValue(), transactions, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    private UUID validateTransferId(String transferId) throws IllegalArgumentException{
            return UUID.fromString(transferId);
    }
}
