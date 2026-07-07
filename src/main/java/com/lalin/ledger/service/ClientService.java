package com.lalin.ledger.service;

import com.lalin.ledger.repo.ClientRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Getter
public class ClientService {

    private final ClientRepo clientRepo;

    @Transactional
    public String createNewClient(String clientName, String email, String description, String contactPerson) {
        return clientRepo.createNewClient(clientName, email, description, contactPerson).toString();
    }
}
