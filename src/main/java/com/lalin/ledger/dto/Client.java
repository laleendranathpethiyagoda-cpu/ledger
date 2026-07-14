package com.lalin.ledger.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record Client(@NotNull(message = "Name required")
                     String clientName,
                     @NotNull(message = "Email required")
                     String email,
                     String description,
                     @NotNull(message = "Contact person required")
                     String contactPerson) implements Serializable {

}
