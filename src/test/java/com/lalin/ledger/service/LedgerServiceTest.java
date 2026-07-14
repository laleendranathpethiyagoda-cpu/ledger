package com.lalin.ledger.service;

import static com.lalin.ledger.repo.InitialData.accounts;
import static com.lalin.ledger.repo.InitialData.tinyLedger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import com.lalin.ledger.account.LedgerEntry;
import com.lalin.ledger.account.TransferType;
import com.lalin.ledger.dto.Transaction;
import com.lalin.ledger.exception.AccountNotFoundException;
import com.lalin.ledger.exception.DuplicateTransactionException;
import com.lalin.ledger.exception.InsufficientFundsException;
import com.lalin.ledger.repo.InitialData;
import com.lalin.ledger.repo.LedgerRepo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

  @Mock
  private LedgerRepo ledgerRepo;

  @Mock
  private InitialData initialData;

  @InjectMocks
  private LedgerService ledgerService;


  @Test
  void deposit_Success() {
    // Given
    tinyLedger.clear();
    String accountNumber = "26082955";
    BigDecimal initialBalance = new BigDecimal("100.00");
    BigDecimal depositAmount = new BigDecimal("50.00");
    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.getCache()).thenReturn(initialData);
    when(initialData.getAccounts()).thenReturn(accounts);
    when(ledgerRepo.getAccountById(account.getAccountNumber())).thenReturn(Optional.of(account));

    // When
    ledgerService.deposit(acNum, transactionId, depositAmount, "Test deposit");

    // Then
    assertEquals(new BigDecimal("150.00"), account.getBalance());
    assertTrue(tinyLedger.containsKey(accountNumber));
    assertEquals(1, tinyLedger.get(accountNumber).size());

    LedgerEntry entry = tinyLedger.get(accountNumber).get(0);
    assertEquals(transactionId, entry.getTransferId());
    assertEquals(depositAmount, entry.getAmount());
    assertEquals(TransferType.DEPOSIT, entry.getTransferType());
    assertEquals("Test deposit", entry.getDescription());
  }

  @Test
  void deposit_AccountNotFound() {
    // Given
    AccountNumber accountNumber = new AccountNumber("12345678");
    UUID transactionId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("50.00");

    when(ledgerRepo.getAccountById(accountNumber)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () ->
        ledgerService.deposit(accountNumber, transactionId, amount, "Test deposit")
    );
  }

  @Test
  void deposit_DuplicateTransaction() {
    // Given
    String accountNumber = "12345678";
    UUID transactionId = UUID.fromString("c865a79e-fdd5-4cc8-991f-da44ac4186e8");
    BigDecimal amount = new BigDecimal("50.00");

    Account account = new Account(new AccountNumber(accountNumber), 123456,
        new BigDecimal("100.00"));
    accounts.put(accountNumber, account);

    // Add existing transaction with same ID
    LedgerEntry existingEntry = new LedgerEntry(transactionId, new AccountNumber(accountNumber),
        amount, TransferType.DEPOSIT, LocalDateTime.now(), "Previous deposit");
    tinyLedger.put(accountNumber, new ArrayList<>(List.of(existingEntry)));

    // When & Then
    var exception = assertThrows(DuplicateTransactionException.class, () ->
        ledgerService.deposit(new AccountNumber(accountNumber), transactionId, amount,
            "Duplicate deposit")
    );

    // Balance should remain unchanged
    assertEquals(new BigDecimal("100.00"), account.getBalance());
    assertEquals("Duplicate transaction c865a79e-fdd5-4cc8-991f-da44ac4186e8", exception.getMsg());
  }

  @Test
  void deposit_FirstTransactionForAccount() {
    // Given
    String accountNumber = "12345678";
    BigDecimal initialBalance = new BigDecimal("0.00");
    BigDecimal depositAmount = new BigDecimal("100.00");
    UUID transactionId = UUID.randomUUID();

    Account account = new Account(new AccountNumber(accountNumber), 123456, initialBalance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.getCache()).thenReturn(initialData);
    when(initialData.getAccounts()).thenReturn(accounts);
    when(ledgerRepo.getAccountById(any(AccountNumber.class))).thenReturn(Optional.of(account));

    // When
    ledgerService.deposit(new AccountNumber(accountNumber), transactionId, depositAmount,
        "First deposit");

    // Then
    assertEquals(new BigDecimal("100.00"), account.getBalance());
    assertTrue(tinyLedger.containsKey(accountNumber));
    assertEquals(1, tinyLedger.get(accountNumber).size());
  }

  @Test
  void withdraw_Success() {
    // Given
    tinyLedger.clear();
    String accountNumber = "26082955";
    BigDecimal initialBalance = new BigDecimal("100.00");
    BigDecimal withdrawAmount = new BigDecimal("30.00");
    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    accounts.put(accountNumber, account);
    when(ledgerRepo.getCache()).thenReturn(initialData);
    when(initialData.getAccounts()).thenReturn(accounts);
    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);

    when(ledgerRepo.getAccountById(acNum)).thenReturn(Optional.of(account));

    // When
    ledgerService.withdraw(acNum, transactionId, withdrawAmount, "Test withdrawal");

    // Then
    assertEquals(new BigDecimal("70.00"), account.getBalance());
    assertTrue(tinyLedger.containsKey(accountNumber));
    assertEquals(1, tinyLedger.get(accountNumber).size());

    LedgerEntry entry = tinyLedger.get(accountNumber).get(0);
    assertEquals(transactionId, entry.getTransferId());
    assertEquals(withdrawAmount, entry.getAmount());
    assertEquals(TransferType.WITHDRAW, entry.getTransferType());
    assertEquals("Test withdrawal", entry.getDescription());
  }

  @Test
  void withdraw_InsufficientFunds() {
    // Given
    tinyLedger.clear();
    String accountNumber = "26082955";
    BigDecimal initialBalance = new BigDecimal("50.00");
    BigDecimal withdrawAmount = new BigDecimal("100.00");
    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);
    when(ledgerRepo.getAccountById(any(AccountNumber.class))).thenReturn(Optional.of(account));

    // When & Then
    assertThrows(InsufficientFundsException.class, () ->
        ledgerService.withdraw(new AccountNumber(accountNumber), transactionId, withdrawAmount,
            "Test withdrawal")
    );

    // Balance should remain unchanged
    assertEquals(new BigDecimal("50.00"), account.getBalance());
    assertFalse(tinyLedger.containsKey(accountNumber));
  }

  @Test
  void withdraw_ExactBalance() {
    // Given
    String accountNumber = "26082955";
    BigDecimal initialBalance = new BigDecimal("100.00");
    BigDecimal withdrawAmount = new BigDecimal("100.00");
    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    accounts.put(accountNumber, account);
    when(ledgerRepo.getCache()).thenReturn(initialData);
    when(initialData.getAccounts()).thenReturn(accounts);
    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);
    when(ledgerRepo.getAccountById(any(AccountNumber.class))).thenReturn(Optional.of(account));

    // When
    ledgerService.withdraw(new AccountNumber(accountNumber), transactionId, withdrawAmount,
        "Withdraw all");

    // Then
    assertEquals(new BigDecimal("0.00"), account.getBalance());
    assertTrue(tinyLedger.containsKey(accountNumber));
  }

  @Test
  void withdraw_AccountNotFound() {
    // Given
    String accountNumber = "11111111";
    BigDecimal initialBalance = new BigDecimal("100.00");
    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    accounts.put(accountNumber, account);
    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);
    when(ledgerRepo.getAccountById(any(AccountNumber.class))).thenReturn(Optional.of(account));
    BigDecimal amount = new BigDecimal("50.00");

    when(ledgerRepo.getAccountById(acNum)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () ->
        ledgerService.withdraw(acNum, transactionId, amount, "Test withdrawal")
    );
  }

  @Test
  void withdraw_DuplicateTransaction() {
    // Given
    String accountNumber = "26082955";
    BigDecimal initialBalance = new BigDecimal("100.00");

    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, initialBalance);
    BigDecimal amount = new BigDecimal("50.00");
    accounts.put(accountNumber, account);

    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);

    // Add existing transaction with same ID
    LedgerEntry existingEntry = new LedgerEntry(transactionId, acNum,
        amount, TransferType.WITHDRAW, LocalDateTime.now(), "Previous withdrawal");
    tinyLedger.put(accountNumber, new ArrayList<>(List.of(existingEntry)));

    // When & Then
    assertThrows(DuplicateTransactionException.class, () ->
        ledgerService.withdraw(new AccountNumber(accountNumber), transactionId, amount,
            "Duplicate withdrawal")
    );

    // Balance should remain unchanged
    assertEquals(new BigDecimal("100.00"), account.getBalance());
  }

  @Test
  void balance_Success() {
    // Given
    String accountNumber = "26082955";
    BigDecimal balance = new BigDecimal("250.00");

    UUID transactionId = UUID.randomUUID();
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, balance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.getAccountById(acNum)).thenReturn(Optional.of(account));

    // When
    Account result = ledgerService.balance(acNum);

    // Then
    assertNotNull(result);
    assertEquals(accountNumber, result.getAccountNumber().accountNumberValue());
    assertEquals(balance, result.getBalance());
  }

  @Test
  void balance_AccountNotFound() {
    // Given
    AccountNumber accountNumber = new AccountNumber("11111111");

    when(ledgerRepo.getAccountById(accountNumber)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () ->
        ledgerService.balance(accountNumber)
    );
  }

  @Test
  void txHistory_Success() {
    // Given
    String accountNumber = "26082955";
    BigDecimal balance = new BigDecimal("250.00");
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, balance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime earlier = now.minusHours(2);
    LocalDateTime earliest = now.minusHours(4);

    List<LedgerEntry> ledgerEntries = new ArrayList<>();
    ledgerEntries.add(new LedgerEntry(UUID.randomUUID(), new AccountNumber(accountNumber),
        new BigDecimal("50.00"), TransferType.DEPOSIT, earliest, "First deposit"));
    ledgerEntries.add(new LedgerEntry(UUID.randomUUID(), new AccountNumber(accountNumber),
        new BigDecimal("30.00"), TransferType.WITHDRAW, earlier, "Withdrawal"));
    ledgerEntries.add(new LedgerEntry(UUID.randomUUID(), new AccountNumber(accountNumber),
        new BigDecimal("80.00"), TransferType.DEPOSIT, now, "Recent deposit"));

    tinyLedger.put(accountNumber, ledgerEntries);

    when(ledgerRepo.getAccountById(acNum)).thenReturn(Optional.of(account));

    // When
    List<Transaction> result = ledgerService.txHistory(acNum);

    // Then
    assertNotNull(result);
    assertEquals(3, result.size());

    // Verify reverse chronological order (most recent first)
    assertEquals("80.00", result.get(0).amount());
    assertEquals(TransferType.DEPOSIT, result.get(0).type());
    assertEquals("Recent deposit", result.get(0).description());

    assertEquals("30.00", result.get(1).amount());
    assertEquals(TransferType.WITHDRAW, result.get(1).type());

    assertEquals("50.00", result.get(2).amount());
    assertEquals(TransferType.DEPOSIT, result.get(2).type());
    assertEquals("First deposit", result.get(2).description());
  }

  @Test
  void txHistory_EmptyHistory() {
    // Given
    String accountNumber = "26082955";
    BigDecimal balance = new BigDecimal("250.00");
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, balance);
    accounts.put(accountNumber, account);

    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);

    tinyLedger.put(accountNumber, new ArrayList<>());

    when(ledgerRepo.getAccountById(any(AccountNumber.class))).thenReturn(Optional.of(account));

    // When
    List<Transaction> result = ledgerService.txHistory(acNum);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void txHistory_AccountNotFound() {
    // Given
    AccountNumber accountNumber = new AccountNumber("11111111");

    when(ledgerRepo.getAccountById(accountNumber)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () ->
        ledgerService.txHistory(accountNumber)
    );
  }

  @Test
  void multipleTransactions_BalanceConsistency() {
    // Given
    tinyLedger.clear();
    String accountNumber = "26082955";
    BigDecimal balance = new BigDecimal("250.00");
    AccountNumber acNum = new AccountNumber(accountNumber);
    Account account = new Account(acNum, 123456, balance);
    accounts.put(accountNumber, account);
    when(ledgerRepo.getCache()).thenReturn(initialData);
    when(initialData.getAccounts()).thenReturn(accounts);
    when(ledgerRepo.tinyLedger()).thenReturn(tinyLedger);

    when(ledgerRepo.getAccountById(acNum)).thenReturn(Optional.of(account));

    // When - Perform multiple transactions
    ledgerService.deposit(new AccountNumber(accountNumber), UUID.randomUUID(),
        new BigDecimal("50.00"), "Deposit 1");
    ledgerService.withdraw(new AccountNumber(accountNumber), UUID.randomUUID(),
        new BigDecimal("30.00"), "Withdraw 1");
    ledgerService.deposit(new AccountNumber(accountNumber), UUID.randomUUID(),
        new BigDecimal("20.00"), "Deposit 2");

    // Then
    assertEquals(new BigDecimal("290.00"), account.getBalance());
    assertEquals(3, tinyLedger.get(accountNumber).size());
    tinyLedger.clear();
  }
}
