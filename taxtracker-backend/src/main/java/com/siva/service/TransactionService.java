package com.siva.service;

import com.siva.dto.TransactionRequestDTO;
import com.siva.dto.TransactionResponseDTO;
import com.siva.entity.Transaction;
import com.siva.entity.User;
import com.siva.repository.TransactionRepository;
import com.siva.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.siva.entity.Form90C;
import com.siva.repository.Form90CRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final Form90CRepository form90cRepository;

    public TransactionResponseDTO addTransaction(String email, TransactionRequestDTO request) {
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

    public Page<TransactionResponseDTO> getTransactions(String email, String financialYear, Integer month, String type, String organizationName, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findTransactionsWithFilters(
                user.getId(), financialYear, month, type, organizationName, pageable);

        return transactions.map(this::mapToResponse);
    }

    public List<TransactionResponseDTO> getAllTransactions(String email, String financialYear, Integer month, String type, String organizationName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findAllTransactionsWithFilters(
                user.getId(), financialYear, month, type, organizationName);

        return transactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TransactionResponseDTO mapToResponse(Transaction t) {
        return TransactionResponseDTO.builder()
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

    public Map<String, Object> getDashboardSummary(String email, String financialYear) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Fetch type totals
        List<Object[]> typeTotals = transactionRepository.sumTaxAmountByType(user.getId(), financialYear);
        BigDecimal tdsDeducted = BigDecimal.ZERO;
        BigDecimal tcsDeducted = BigDecimal.ZERO;
        BigDecimal tdsTotalAmount = BigDecimal.ZERO;
        BigDecimal tcsTotalAmount = BigDecimal.ZERO;

        for (Object[] row : typeTotals) {
            String type = (String) row[0];
            BigDecimal taxSum = (BigDecimal) row[1];
            BigDecimal amountSum = (BigDecimal) row[2];
            if (taxSum == null) taxSum = BigDecimal.ZERO;
            if (amountSum == null) amountSum = BigDecimal.ZERO;
            
            if ("TDS".equalsIgnoreCase(type)) {
                tdsDeducted = taxSum;
                tdsTotalAmount = amountSum;
            } else if ("TCS".equalsIgnoreCase(type)) {
                tcsDeducted = taxSum;
                tcsTotalAmount = amountSum;
            }
        }

        // TODO: replace with real tax-saved formula if business logic changes
        BigDecimal totalTaxSaved = tdsDeducted.add(tcsDeducted);

        BigDecimal tdsPercentage = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (tdsTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            tdsPercentage = tdsDeducted.multiply(new BigDecimal("100")).divide(tdsTotalAmount, 2, RoundingMode.HALF_UP);
        }

        BigDecimal tcsPercentage = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (tcsTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            tcsPercentage = tcsDeducted.multiply(new BigDecimal("100")).divide(tcsTotalAmount, 2, RoundingMode.HALF_UP);
        }

        // 2. Active Form Status
        String activeFormStatus = "NONE";
        Optional<Form90C> formOpt = form90cRepository.findByUserEmailAndFinancialYear(email, financialYear);
        if (formOpt.isPresent()) {
            String dbStatus = formOpt.get().getStatus();
            if ("SUBMITTED".equalsIgnoreCase(dbStatus)) {
                activeFormStatus = "SUBMITTED";
            } else {
                activeFormStatus = "PENDING";
            }
        }

        // 3. Tax Savings Trend (Grouped by month, Indian FY Apr-Mar)
        List<Object[]> monthTotals = transactionRepository.sumTaxAmountByMonthAndYear(user.getId(), financialYear);
        Map<String, BigDecimal> monthMap = new HashMap<>();
        for (Object[] row : monthTotals) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            BigDecimal sum = (BigDecimal) row[2];
            if (sum == null) sum = BigDecimal.ZERO;
            monthMap.put(year + "-" + month, sum);
        }

        List<Map<String, Object>> taxSavingsTrend = new ArrayList<>();
        
        if (financialYear != null && financialYear.matches("^\\d{4}-\\d{4}$")) {
            String[] years = financialYear.split("-");
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);

            // Indian FY: Apr to Mar
            int[] fyMonths = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};
            
            for (int month : fyMonths) {
                int year = (month >= 4) ? startYear : endYear;
                String key = year + "-" + month;
                BigDecimal amount = monthMap.getOrDefault(key, BigDecimal.ZERO);
                
                String monthName = java.time.Month.of(month).name().substring(0, 3); // "APR", "MAY", etc.
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("month", monthName);
                point.put("amount", amount);
                taxSavingsTrend.add(point);
            }
        }
        
        // Calculate % change vs previous month
        BigDecimal trendPercentageChange = BigDecimal.ZERO;
        if (!taxSavingsTrend.isEmpty()) {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            int currentIndex = -1;
            
            if (financialYear != null && financialYear.matches("^\\d{4}-\\d{4}$")) {
                int startYear = Integer.parseInt(financialYear.split("-")[0]);
                int endYear = Integer.parseInt(financialYear.split("-")[1]);
                
                if (currentYear == startYear && currentMonth >= 4) {
                    currentIndex = currentMonth - 4; // Apr is 0, May is 1...
                } else if (currentYear == endYear && currentMonth <= 3) {
                    currentIndex = currentMonth + 8; // Jan is 9, Feb is 10, Mar is 11
                } else if (currentYear > endYear) {
                    currentIndex = 11; // FY is in the past, use Mar
                }
            }
            
            if (currentIndex > 0 && currentIndex < taxSavingsTrend.size()) {
                BigDecimal currentMonthTotal = (BigDecimal) taxSavingsTrend.get(currentIndex).get("amount");
                BigDecimal previousMonthTotal = (BigDecimal) taxSavingsTrend.get(currentIndex - 1).get("amount");
                
                if (previousMonthTotal.compareTo(BigDecimal.ZERO) > 0) {
                    trendPercentageChange = currentMonthTotal.subtract(previousMonthTotal)
                            .divide(previousMonthTotal, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100));
                }
                // If previousMonthTotal is 0, trendPercentageChange remains 0 (guard against divide-by-zero)
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalTaxSaved", totalTaxSaved);
        summary.put("tdsDeducted", tdsDeducted);
        summary.put("tdsPercentage", tdsPercentage);
        summary.put("tcsDeducted", tcsDeducted);
        summary.put("tcsPercentage", tcsPercentage);
        summary.put("activeFormStatus", activeFormStatus);
        summary.put("taxSavingsTrend", taxSavingsTrend);
        summary.put("trendPercentageChange", trendPercentageChange);

        return summary;
    }
    
    public List<String> getAvailableFinancialYears(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepository.findDistinctFinancialYearsByUserId(user.getId());
    }
}
