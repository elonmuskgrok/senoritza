package com.siva.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private String type;
    private String organizationName;
    private String financialYear;
    private Integer txnMonth;
}
