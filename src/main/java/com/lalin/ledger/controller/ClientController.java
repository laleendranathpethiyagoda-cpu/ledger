package com.lalin.ledger.controller;

import com.lalin.ledger.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
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

}
