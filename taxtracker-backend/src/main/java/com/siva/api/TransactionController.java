package com.siva.api;

import com.siva.dto.TransactionRequestDTO;
import com.siva.dto.ApiResponse;
import com.siva.dto.TransactionResponseDTO;
import com.siva.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @Valid @RequestBody TransactionRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        TransactionResponseDTO response = transactionService.addTransaction(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @RequestParam(required = false) String financialYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String organizationName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        
        String email = authentication.getName();
        Page<TransactionResponseDTO> response = transactionService.getTransactions(
                email, financialYear, month, type, organizationName, pageNumber, pageSize);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<java.util.Map<String, Object>> getDashboardSummary(
            @RequestParam String financialYear,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(transactionService.getDashboardSummary(email, financialYear));
    }

    @GetMapping("/financial-years")
    public ResponseEntity<java.util.List<String>> getAvailableFinancialYears(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getAvailableFinancialYears(authentication.getName()));
    }
}
