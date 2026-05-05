package com.lalin.ledger.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private Map<String, String> messages;
    private String error;
    private int status;
    private LocalDateTime timestamp;

}
