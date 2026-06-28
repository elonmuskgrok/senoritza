package com.siva.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Form90CDraftRequestDTO {

    private String name;
    private String mobileNumber;
    private String financialYear;
    private List<TransactionHistoryDraftRequest> transactionHistory;

    @Data
    public static class TransactionHistoryDraftRequest {
        private String organizationName;
        private BigDecimal amount;
        private BigDecimal taxAmount;
        private String type;
    }
}
