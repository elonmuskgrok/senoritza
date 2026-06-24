package com.taxtracker.service;

import com.taxtracker.dto.request.TransactionRequest;
import com.taxtracker.dto.response.TransactionResponse;
import com.taxtracker.entity.Transaction;
import com.taxtracker.entity.User;
import com.taxtracker.repository.TransactionRepository;
import com.taxtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse addTransaction(String email, TransactionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int month = request.getTransactionDate().getMonthValue();
        int year = request.getTransactionDate().getYear();
        
        String financialYear;
        if (month >= 4) {
            financialYear = year + "-" + (year + 1);
        } else {
            financialYear = (year - 1) + "-" + year;
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionDate(request.getTransactionDate())
                .amount(request.getAmount())
                .taxAmount(request.getTaxAmount())
                .type(request.getType())
                .organizationName(request.getOrganizationName())
                .financialYear(financialYear)
                .txnMonth(month)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    public Page<TransactionResponse> getTransactions(String email, String financialYear, Integer month, String type, String organizationName, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findTransactionsWithFilters(
                user.getId(), financialYear, month, type, organizationName, pageable);

        return transactions.map(this::mapToResponse);
    }

    public List<TransactionResponse> getAllTransactions(String email, String financialYear, Integer month, String type, String organizationName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findAllTransactionsWithFilters(
                user.getId(), financialYear, month, type, organizationName);

        return transactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .transactionDate(t.getTransactionDate())
                .amount(t.getAmount())
                .taxAmount(t.getTaxAmount())
                .type(t.getType())
                .organizationName(t.getOrganizationName())
                .financialYear(t.getFinancialYear())
                .txnMonth(t.getTxnMonth())
                .build();
    }
}
