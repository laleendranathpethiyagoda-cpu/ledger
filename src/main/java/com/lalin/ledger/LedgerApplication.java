package com.lalin.ledger;

import com.lalin.ledger.account.Account;
import com.lalin.ledger.account.AccountNumber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class LedgerApplication {

	public static void main(String[] args) {

		SpringApplication.run(LedgerApplication.class, args);
	}

}
