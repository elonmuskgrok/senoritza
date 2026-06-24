package com.taxtracker.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Please provide a valid Transaction Date")
    private LocalDate transactionDate;

    @NotNull(message = "Please provide a valid Amount")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Please provide a valid Tax Amount")
    @PositiveOrZero(message = "Tax Amount cannot be negative")
    private BigDecimal taxAmount;

    @NotBlank(message = "Please provide a valid Type")
    private String type;

    @NotBlank(message = "Please provide a valid Organization Name")
    private String organizationName;
}
