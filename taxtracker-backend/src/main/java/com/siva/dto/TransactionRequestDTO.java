package com.siva.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequestDTO {

    @NotNull(message = "Please provide a valid Transaction Date")
    private LocalDate transactionDate;

    @NotNull(message = "Please provide a valid Amount")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Please provide a valid Tax Amount")
    @PositiveOrZero(message = "Tax Amount cannot be negative")
    private BigDecimal taxAmount;

    @NotBlank(message = "Please provide a valid Type")
    @Pattern(regexp = "^(TDS|TCS)$", message = "Type must be TDS or TCS")
    private String type;

    @NotBlank(message = "Please provide a valid Organization Name")
    private String organizationName;
}
