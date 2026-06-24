package com.taxtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taxtracker.dto.response.TransactionResponse;
import com.taxtracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions/download")
@RequiredArgsConstructor
public class DataExportController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<byte[]> downloadTransactions(
            @RequestParam(required = true) String format,
            @RequestParam(required = false) String financialYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String organizationName,
            Authentication authentication) throws Exception {

        if (!"JSON".equalsIgnoreCase(format) && !"CSV".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().body("Unsupported format".getBytes());
        }

        String email = authentication.getName();
        List<TransactionResponse> transactions = transactionService.getAllTransactions(
                email, financialYear, month, type, organizationName);

        byte[] outputData;
        HttpHeaders headers = new HttpHeaders();

        if ("JSON".equalsIgnoreCase(format)) {
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("totalRecords", transactions.size());
            exportData.put("transactions", transactions);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            outputData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportData);

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "transactions.json");
        } else {
            StringBuilder csv = new StringBuilder();
            csv.append("Date,Organization Name,Type,Amount,Tax Amount,Financial Year\n");
            for (TransactionResponse txn : transactions) {
                csv.append(String.format("%s,%s,%s,%.2f,%.2f,%s\n",
                        txn.getTransactionDate(),
                        txn.getOrganizationName() != null ? txn.getOrganizationName().replace(",", " ") : "", // escape commas
                        txn.getType(),
                        txn.getAmount() != null ? txn.getAmount().doubleValue() : 0.0,
                        txn.getTaxAmount() != null ? txn.getTaxAmount().doubleValue() : 0.0,
                        txn.getFinancialYear()
                ));
            }
            outputData = csv.toString().getBytes();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "transactions.csv");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputData);
    }
}
