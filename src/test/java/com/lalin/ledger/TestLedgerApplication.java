package com.lalin.ledger;

import org.springframework.boot.SpringApplication;

public class TestLedgerApplication {

	public static void main(String[] args) {
		SpringApplication.from(LedgerApplication::main).run(args);
	}

}
