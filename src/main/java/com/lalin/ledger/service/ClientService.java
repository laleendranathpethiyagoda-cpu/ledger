package com.lalin.ledger.service;

import com.lalin.ledger.repo.ClientRepo;
import com.lalin.ledger.repo.LedgerRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter
public class ClientService {

    private final ClientRepo clientRepo;
}
