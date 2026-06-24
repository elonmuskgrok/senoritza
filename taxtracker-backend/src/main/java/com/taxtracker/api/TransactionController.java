package com.taxtracker.api;

import com.taxtracker.dto.request.TransactionRequest;
import com.taxtracker.dto.response.ApiResponse;
import com.taxtracker.dto.response.TransactionResponse;
import com.taxtracker.service.TransactionService;
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
    public ResponseEntity<TransactionResponse> addTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        TransactionResponse response = transactionService.addTransaction(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String financialYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String organizationName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        
        String email = authentication.getName();
        Page<TransactionResponse> response = transactionService.getTransactions(
                email, financialYear, month, type, organizationName, pageNumber, pageSize);
        
        return ResponseEntity.ok(response);
    }
}
