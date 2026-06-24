package com.taxtracker.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Form90cRequest {

    @NotBlank(message = "Please provide a valid Name")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+){0,2}$", message = "Please provide a valid Name")
    private String name;

    @NotBlank(message = "Please provide a valid Mobile Number")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please provide a valid Mobile Number")
    private String mobileNumber;

    @NotBlank(message = "Please provide a valid Financial Year")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Please provide a valid Financial Year")
    private String financialYear;

    @NotEmpty(message = "No transactions added.")
    @Valid
    private List<TransactionHistoryRequest> transactionHistory;

    @Data
    public static class TransactionHistoryRequest {
        @NotBlank(message = "Please provide a valid Organization Name")
        private String organizationName;

        @NotNull(message = "Please provide a valid Amount")
        @Positive(message = "Please provide a valid Amount")
        private BigDecimal amount;

        @NotNull(message = "Please provide a valid Tax Amount")
        @PositiveOrZero(message = "Please provide a valid Tax Amount")
        private BigDecimal taxAmount;

        @NotBlank(message = "Please provide a valid Type")
        @Pattern(regexp = "^(TDS|TCS|OTHER)$", message = "Please provide a valid Type")
        private String type;
    }
}
