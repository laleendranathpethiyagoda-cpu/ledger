package com.lalin.ledger.controller;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.TransferType;
import com.lalin.ledger.dto.Transaction;
import com.lalin.ledger.dto.TransferRequest;
import com.lalin.ledger.dto.TransferResponse;
import com.lalin.ledger.exception.AccountNotFoundException;
import com.lalin.ledger.exception.DuplicateTransactionException;
import com.lalin.ledger.exception.InsufficientFundsException;
import com.lalin.ledger.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LedgerService ledgerService;

    @Test
    void deposit_Success() throws Exception {
        String accountId = "26082099";
        String transferId = UUID.randomUUID().toString();
        var amount = new BigDecimal("100.00");
        TransferRequest request = new TransferRequest(transferId, amount, "Test deposit");

        doNothing().when(ledgerService).deposit(any(AccountNumber.class), any(UUID.class), any(BigDecimal.class), anyString());

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transferId))
                .andExpect(jsonPath("$.amount").value("100.00"))
                .andExpect(jsonPath("$.message").value("Deposit Success"));

        verify(ledgerService, times(1)).deposit(any(AccountNumber.class), any(UUID.class), eq(new BigDecimal("100.00")), eq("Test deposit"));
    }

    @Test
    void deposit_InvalidTransferId() throws Exception {
        String accountId = "26082099";
        TransferRequest request = new TransferRequest("invalid-uuid", new BigDecimal("100.00"), "Test deposit");

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(ledgerService, never()).deposit(any(), any(), any(), any());
    }

    @Test
    void deposit_MissingTransferId() throws Exception {
        String accountId = "26082099";
        TransferRequest request = new TransferRequest(null, new BigDecimal("100.00"), "Test deposit");

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(ledgerService, never()).deposit(any(), any(), any(), any());
    }

    @Test
    void deposit_AmountLessThanMinimum() throws Exception {
        String accountId = "26082099";
        String transferId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transferId, new BigDecimal("0.50"), "Test deposit");

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(ledgerService, never()).deposit(any(), any(), any(), any());
    }

    @Test
    void deposit_DuplicateTransaction() throws Exception {
        String accountId = "26082099";
        String transferId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transferId, new BigDecimal("100.00"), "Test deposit");

        doThrow(new DuplicateTransactionException("Duplicate transaction " + transferId))
                .when(ledgerService).deposit(any(AccountNumber.class), any(UUID.class), any(BigDecimal.class), anyString());

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_Success() throws Exception {
        String accountId = "26082955";
        var acNumber = new AccountNumber(accountId);
        String transferId = UUID.randomUUID().toString();
        UUID tfID = UUID.fromString(transferId);
        var amount = new BigDecimal("50.00");
        TransferRequest request = new TransferRequest(tfID.toString(), amount, "Atm cash withdrawal");
        TransferResponse response = new TransferResponse(tfID.toString(), amount.toString(), "Withdrawal success");

        doNothing().when(ledgerService).withdraw(acNumber, tfID, amount, "Atm cash withdrawal");

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tfID.toString()))
                .andExpect(jsonPath("$.amount").value("50.00"))
                .andExpect(jsonPath("$.message").value("Withdrawal success"));

        verify(ledgerService, times(1)).withdraw(acNumber, tfID, amount, "Atm cash withdrawal");
    }

    @Test
    void withdraw_InsufficientFunds() throws Exception {
        String accountId = "ACC001";
        String transferId = UUID.randomUUID().toString();
        TransferRequest request = new TransferRequest(transferId, new BigDecimal("1000.00"), "Test withdrawal");

        doThrow(new InsufficientFundsException("Insufficient funds for withdrawal " + transferId))
                .when(ledgerService).withdraw(any(AccountNumber.class), any(UUID.class), any(BigDecimal.class), anyString());

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_InvalidTransferId() throws Exception {
        String accountId = "ACC001";
        TransferRequest request = new TransferRequest("not-a-uuid", new BigDecimal("50.00"), "Test withdrawal");

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(ledgerService, never()).withdraw(any(), any(), any(), any());
    }

    @Test
    void balance_Success() throws Exception {
        String accountId = "26082099";
        Account account = new Account(new AccountNumber(accountId), 123456, new BigDecimal("500.00"));

        when(ledgerService.balance(any(AccountNumber.class))).thenReturn(account);

        mockMvc.perform(get("/api/v1/accounts/{id}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountId))
                .andExpect(jsonPath("$.balance").value("500.00"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(ledgerService, times(1)).balance(any(AccountNumber.class));
    }

    @Test
    void balance_AccountNotFound() throws Exception {
        String accountId = "26082977";
        var acNumber = new AccountNumber(accountId);

        when(ledgerService.balance(acNumber))
                .thenThrow(new AccountNotFoundException("Account does not exist"));

        mockMvc.perform(get("/api/v1/accounts/{id}/balance", accountId))
                .andExpect(status().isBadRequest());

        verify(ledgerService, times(1)).balance(acNumber);
    }

    @Test
    void txHistory_Success() throws Exception {
        String accountId = "26082099";
        List<Transaction> transactions = List.of(
                new Transaction(accountId, "100.00", TransferType.DEPOSIT, LocalDateTime.now(), "Deposit 1"),
                new Transaction(accountId, "50.00", TransferType.WITHDRAW, LocalDateTime.now(), "Withdrawal 1")
        );

        when(ledgerService.txHistory(new AccountNumber(accountId))).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/accounts/{id}/history", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountId))
                .andExpect(jsonPath("$.txHistory").isArray())
                .andExpect(jsonPath("$.txHistory.length()").value(2))
                .andExpect(jsonPath("$.txHistory[0].amount").value("100.00"))
                .andExpect(jsonPath("$.txHistory[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$.txHistory[1].amount").value("50.00"))
                .andExpect(jsonPath("$.txHistory[1].type").value("WITHDRAW"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(ledgerService, times(1)).txHistory(any(AccountNumber.class));
    }

    @Test
    void txHistory_AccountNotFound() throws Exception {
        String accountId = "26082977";

        var acNumber = new AccountNumber(accountId);
        when(ledgerService.txHistory(acNumber))
                .thenThrow(new AccountNotFoundException("Invalid bank account number format"));

        mockMvc.perform(get("/api/v1/accounts/{id}/history", accountId))
                .andExpect(status().isBadRequest());

        verify(ledgerService, times(1)).txHistory(acNumber);
    }

    @Test
    void txHistory_EmptyHistory() throws Exception {
        String accountId = "26082099";

        when(ledgerService.txHistory(any(AccountNumber.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/accounts/{id}/history", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountId))
                .andExpect(jsonPath("$.txHistory").isArray())
                .andExpect(jsonPath("$.txHistory.length()").value(0));

        verify(ledgerService, times(1)).txHistory(any(AccountNumber.class));
    }
}
