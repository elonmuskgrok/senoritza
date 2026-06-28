package com.siva.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Form90CResponseDTO {
    private Long formId;
    private String name;
    private String mobileNumber;
    private String financialYear;
    private String status;
    private List<TransactionHistoryResponse> transactionHistory;

    @Data
    @Builder
    public static class TransactionHistoryResponse {
        private String organizationName;
        private BigDecimal amount;
        private BigDecimal taxAmount;
        private String type;
    }
}
