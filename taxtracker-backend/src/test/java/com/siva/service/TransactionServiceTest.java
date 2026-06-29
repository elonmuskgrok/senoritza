package com.siva.service;

import com.siva.dto.TransactionRequestDTO;
import com.siva.dto.TransactionResponseDTO;
import com.siva.entity.Transaction;
import com.siva.entity.User;
import com.siva.repository.TransactionRepository;
import com.siva.repository.UserRepository;
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

    @Mock
    private com.siva.repository.Form90CRepository form90cRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testAddTransaction_Success() {
        TransactionRequestDTO request = new TransactionRequestDTO();
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

        TransactionResponseDTO response = transactionService.addTransaction("test@test.com", request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("2023-2024", response.getFinancialYear());
    }

    @Test
    void testAddTransaction_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.addTransaction("unknown@test.com", new TransactionRequestDTO()));
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

        Page<TransactionResponseDTO> responsePage = transactionService.getTransactions("test@test.com", "2023-2024", 5, "TDS", "Org", 0, 10);

        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void testGetAllTransactions() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        when(transactionRepository.findAllTransactionsWithFilters(eq(1L), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        List<TransactionResponseDTO> responses = transactionService.getAllTransactions("test@test.com", null, null, null, null);

        assertEquals(2, responses.size());
    }

    @Test
    void testAddTransaction_JanToMar() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionDate(LocalDate.of(2024, 2, 15)); // February
        request.setAmount(new BigDecimal("1000.00"));
        request.setTaxAmount(new BigDecimal("100.00"));
        request.setType("TCS");
        request.setOrganizationName("Test Org");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Transaction savedTxn = new Transaction();
        savedTxn.setId(11L);
        savedTxn.setFinancialYear("2023-2024");
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTxn);

        TransactionResponseDTO response = transactionService.addTransaction("test@test.com", request);
        assertEquals("2023-2024", response.getFinancialYear());
    }

    @Test
    void testGetDashboardSummary_FullData() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        // Mock Type Totals: Object[] { String type, BigDecimal taxSum, BigDecimal amountSum }
        List<Object[]> typeTotals = List.of(
            new Object[]{"TDS", new BigDecimal("150.00"), new BigDecimal("1500.00")},
            new Object[]{"TCS", new BigDecimal("50.00"), new BigDecimal("1000.00")}
        );
        when(transactionRepository.sumTaxAmountByType(1L, "2023-2024")).thenReturn(typeTotals);

        // Mock Form90C
        com.siva.entity.Form90C form = new com.siva.entity.Form90C();
        form.setStatus("SUBMITTED");
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024"))
            .thenReturn(Optional.of(form));

        // Mock Month Totals: Object[] { Integer year, Integer month, BigDecimal sum }
        List<Object[]> monthTotals = List.of(
            new Object[]{2023, 4, new BigDecimal("100.00")}, // Apr 2023
            new Object[]{2023, 5, new BigDecimal("100.00")}  // May 2023
        );
        when(transactionRepository.sumTaxAmountByMonthAndYear(1L, "2023-2024")).thenReturn(monthTotals);

        java.util.Map<String, Object> summary = transactionService.getDashboardSummary("test@test.com", "2023-2024");

        assertNotNull(summary);
        assertEquals(new BigDecimal("200.00"), summary.get("totalTaxSaved")); // 150 + 50
        assertEquals("SUBMITTED", summary.get("activeFormStatus"));
        
        // TDS % = 150/1500 * 100 = 10.00
        assertEquals(new BigDecimal("10.00"), summary.get("tdsPercentage"));
        
        // TCS % = 50/1000 * 100 = 5.00
        assertEquals(new BigDecimal("5.00"), summary.get("tcsPercentage"));
    }

    @Test
    void testGetDashboardSummary_EmptyData() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        when(transactionRepository.sumTaxAmountByType(1L, "2023-2024")).thenReturn(List.of());
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024")).thenReturn(Optional.empty());
        when(transactionRepository.sumTaxAmountByMonthAndYear(1L, "2023-2024")).thenReturn(List.of());

        java.util.Map<String, Object> summary = transactionService.getDashboardSummary("test@test.com", "2023-2024");
        
        assertEquals(BigDecimal.ZERO, summary.get("totalTaxSaved"));
        assertEquals("NONE", summary.get("activeFormStatus"));
        assertEquals(new BigDecimal("0.00"), summary.get("tdsPercentage"));
    }

    @Test
    void testGetAvailableFinancialYears() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findDistinctFinancialYearsByUserId(1L)).thenReturn(List.of("2023-2024"));

        List<String> years = transactionService.getAvailableFinancialYears("test@test.com");
        assertEquals(1, years.size());
        assertEquals("2023-2024", years.get(0));
    }
    @Test
    void testAddTransaction_NullRequest() {
        assertThrows(RuntimeException.class, () -> transactionService.addTransaction("test@test.com", null));
    }

    @Test
    void testGetDashboardSummary_Failure_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getDashboardSummary("unknown@test.com", "2023-2024"));
    }

    @Test
    void testGetAvailableFinancialYears_EmptyList() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findDistinctFinancialYearsByUserId(1L)).thenReturn(List.of());

        List<String> years = transactionService.getAvailableFinancialYears("test@test.com");
        assertTrue(years.isEmpty());
    }

    @Test
    void testGetTransactions_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getTransactions("unknown@test.com", null, null, null, null, 0, 10));
    }

    @Test
    void testGetAllTransactions_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getAllTransactions("unknown@test.com", null, null, null, null));
    }

    @Test
    void testGetAvailableFinancialYears_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getAvailableFinancialYears("unknown@test.com"));
    }
}
