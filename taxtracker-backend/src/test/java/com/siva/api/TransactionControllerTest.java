package com.siva.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.dto.TransactionRequestDTO;
import com.siva.dto.TransactionResponseDTO;
import com.siva.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.siva.security.CustomUserDetailsService;
import com.siva.security.JwtTokenProvider;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com")
    void testAddTransaction_Http201() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionDate(LocalDate.now());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setType("TDS");
        request.setOrganizationName("Test Org");

        TransactionResponseDTO response = TransactionResponseDTO.builder().id(1L).build();

        when(transactionService.addTransaction(any(), any(TransactionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetTransactions_Http200() throws Exception {
        TransactionResponseDTO response = TransactionResponseDTO.builder().id(1L).build();
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));

        when(transactionService.getTransactions(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .param("pageNumber", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetDashboardSummary_Http200() throws Exception {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("totalTaxSaved", BigDecimal.TEN);
        
        when(transactionService.getDashboardSummary(any(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/transactions/dashboard-summary")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .param("financialYear", "2023-2024"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetAvailableFinancialYears_Http200() throws Exception {
        when(transactionService.getAvailableFinancialYears(any())).thenReturn(List.of("2022-2023", "2023-2024"));

        mockMvc.perform(get("/api/transactions/financial-years")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList())))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "test@test.com")
    void testAddTransaction_Http400_NegativeAmount() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("-100.00")); // negative
        request.setTransactionDate(LocalDate.now());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setType("TDS");
        request.setOrganizationName("Test Org");

        mockMvc.perform(post("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testAddTransaction_Http400_InvalidType() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionDate(LocalDate.now());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setType("INVALID"); // Not TDS or TCS
        request.setOrganizationName("Test Org");

        mockMvc.perform(post("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetDashboardSummary_Http400_MissingFinancialYear() throws Exception {
        // Missing the required financialYear param
        mockMvc.perform(get("/api/transactions/dashboard-summary")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetAvailableFinancialYears_Http200_Empty() throws Exception {
        when(transactionService.getAvailableFinancialYears(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/financial-years")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList())))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "test@test.com")
    void testGetTransactions_WithFilters() throws Exception {
        TransactionResponseDTO response = TransactionResponseDTO.builder().id(1L).build();
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));

        when(transactionService.getTransactions(eq("test@test.com"), eq("2023-2024"), eq(5), eq("TDS"), eq("Org"), eq(1), eq(20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .param("financialYear", "2023-2024")
                .param("month", "5")
                .param("type", "TDS")
                .param("organizationName", "Org")
                .param("pageNumber", "1")
                .param("pageSize", "20"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetTransactions_MissingParams_UsesDefaults() throws Exception {
        TransactionResponseDTO response = TransactionResponseDTO.builder().id(1L).build();
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(response));

        // When missing, it should pass nulls and defaults for paging
        when(transactionService.getTransactions(eq("test@test.com"), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testAddTransaction_Failure_Runtime() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionDate(LocalDate.now());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setType("TDS");
        request.setOrganizationName("Test Org");

        when(transactionService.addTransaction(any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/transactions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
