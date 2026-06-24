package com.taxtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxtracker.dto.request.TransactionRequest;
import com.taxtracker.dto.response.TransactionResponse;
import com.taxtracker.service.TransactionService;
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

import com.taxtracker.security.CustomUserDetailsService;
import com.taxtracker.security.JwtTokenProvider;

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
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionDate(LocalDate.now());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setType("TDS");
        request.setOrganizationName("Test Org");

        TransactionResponse response = TransactionResponse.builder().id(1L).build();

        when(transactionService.addTransaction(any(), any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetTransactions_Http200() throws Exception {
        TransactionResponse response = TransactionResponse.builder().id(1L).build();
        Page<TransactionResponse> page = new PageImpl<>(List.of(response));

        when(transactionService.getTransactions(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/transactions")
                .param("pageNumber", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}
