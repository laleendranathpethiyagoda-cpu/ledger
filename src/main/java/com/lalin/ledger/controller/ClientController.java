package com.lalin.ledger.controller;

import com.lalin.ledger.dto.Client;
import com.lalin.ledger.service.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Controller for ledger and client-based operations
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
@Validated
public class ClientController {

    private final ClientService clientService;

    @RequestMapping("/create")
    public ResponseEntity<String> createClient(@Valid @RequestBody Client client) {
        var response =  clientService.createNewClient(client.clientName(),
                client.email(), client.description(), client.contactPerson());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
