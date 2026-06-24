package com.taxtracker.service;

import com.taxtracker.dto.request.TransactionRequest;
import com.taxtracker.dto.response.TransactionResponse;
import com.taxtracker.entity.Transaction;
import com.taxtracker.entity.User;
import com.taxtracker.repository.TransactionRepository;
import com.taxtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testAddTransaction_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionDate(LocalDate.of(2023, 5, 15));
        request.setAmount(new BigDecimal("1000.00"));
        request.setTaxAmount(new BigDecimal("100.00"));
        request.setType("TDS");
        request.setOrganizationName("Test Org");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Transaction savedTxn = new Transaction();
        savedTxn.setId(10L);
        savedTxn.setTransactionDate(request.getTransactionDate());
        savedTxn.setAmount(request.getAmount());
        savedTxn.setTaxAmount(request.getTaxAmount());
        savedTxn.setType(request.getType());
        savedTxn.setOrganizationName(request.getOrganizationName());
        savedTxn.setFinancialYear("2023-2024");
        savedTxn.setTxnMonth(5);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTxn);

        TransactionResponse response = transactionService.addTransaction("test@test.com", request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("2023-2024", response.getFinancialYear());
    }

    @Test
    void testAddTransaction_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.addTransaction("unknown@test.com", new TransactionRequest()));
    }

    @Test
    void testGetTransactions_WithFilters() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Transaction txn = new Transaction();
        txn.setId(10L);
        Page<Transaction> page = new PageImpl<>(List.of(txn));

        when(transactionRepository.findTransactionsWithFilters(eq(1L), anyString(), anyInt(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<TransactionResponse> responsePage = transactionService.getTransactions("test@test.com", "2023-2024", 5, "TDS", "Org", 0, 10);

        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void testGetAllTransactions() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        when(transactionRepository.findAllTransactionsWithFilters(eq(1L), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        List<TransactionResponse> responses = transactionService.getAllTransactions("test@test.com", null, null, null, null);

        assertEquals(2, responses.size());
    }
}
