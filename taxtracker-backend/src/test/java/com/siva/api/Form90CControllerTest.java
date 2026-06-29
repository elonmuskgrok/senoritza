package com.siva.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.dto.Form90CRequestDTO;
import com.siva.dto.SubmissionRequestDTO;
import com.siva.service.Form90CService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.siva.security.CustomUserDetailsService;
import com.siva.security.JwtTokenProvider;

@WebMvcTest(Form90CController.class)
@AutoConfigureMockMvc(addFilters = false)
public class Form90CControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Form90CService form90cService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com")
    void testSaveDraft_Http200() throws Exception {
        com.siva.dto.Form90CRequestDTO request = new com.siva.dto.Form90CRequestDTO();
        request.setFinancialYear("2023-2024");
        request.setName("Test");
        request.setMobileNumber("1234567890");
        request.setTransactionHistory(java.util.Collections.emptyList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("formId", 1);
        
        when(form90cService.saveDraft(any(), any(com.siva.dto.Form90CRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/forms/90c/draft")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testSubmit_Http200() throws Exception {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setFormId(1L);
        
        Map<String, Object> response = new HashMap<>();
        response.put("submissionId", 5);

        when(form90cService.submitForm(any(), any(SubmissionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/submissions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testSaveForm_Http200() throws Exception {
        Form90CRequestDTO request = new Form90CRequestDTO();
        request.setFinancialYear("2023-2024");
        request.setName("Test");
        request.setMobileNumber("9876543210");
        com.siva.dto.Form90CRequestDTO.TransactionHistoryRequest tx = new com.siva.dto.Form90CRequestDTO.TransactionHistoryRequest();
        tx.setOrganizationName("Test Org");
        tx.setAmount(new java.math.BigDecimal("1000"));
        tx.setTaxAmount(new java.math.BigDecimal("100"));
        tx.setType("TDS");
        request.setTransactionHistory(java.util.Collections.singletonList(tx));
        
        Map<String, Object> response = new HashMap<>();
        response.put("formId", 1);
        
        when(form90cService.saveForm(any(), any(Form90CRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/forms/90c")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetForm_Http200() throws Exception {
        com.siva.dto.Form90CResponseDTO response = com.siva.dto.Form90CResponseDTO.builder().formId(1L).build();
        
        when(form90cService.getForm(any(), anyString())).thenReturn(response);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/forms/90c")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .param("financialYear", "2023-2024"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testUploadDocument_Http200() throws Exception {
        com.siva.dto.UploadRequestDTO request = new com.siva.dto.UploadRequestDTO();
        request.setFormId(1L);
        request.setName("test.pdf");
        request.setData("JVBERi0xLjQKJ..."); // dummy base64
        
        Map<String, Object> response = new HashMap<>();
        response.put("documentId", 1);
        
        when(form90cService.uploadDocument(any(), any(com.siva.dto.UploadRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/uploads")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "test@test.com")
    void testSaveForm_Http400_ValidationFailed() throws Exception {
        Form90CRequestDTO request = new Form90CRequestDTO();
        // Missing name and mobileNumber
        request.setFinancialYear("2023-2024");
        
        mockMvc.perform(post("/api/forms/90c")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testUploadDocument_Http400_ValidationFailed() throws Exception {
        com.siva.dto.UploadRequestDTO request = new com.siva.dto.UploadRequestDTO();
        request.setFormId(1L);
        // Missing name and data
        
        mockMvc.perform(post("/api/uploads")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testGetForm_Http400_MissingFinancialYear() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/forms/90c")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList())))
                // Missing param
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "test@test.com")
    void testSaveForm_Failure_Runtime() throws Exception {
        Form90CRequestDTO request = new Form90CRequestDTO();
        request.setFinancialYear("2023-2024");
        request.setName("Test");
        request.setMobileNumber("9876543210");
        com.siva.dto.Form90CRequestDTO.TransactionHistoryRequest tx = new com.siva.dto.Form90CRequestDTO.TransactionHistoryRequest();
        tx.setOrganizationName("Test Org");
        tx.setAmount(new java.math.BigDecimal("1000"));
        tx.setTaxAmount(new java.math.BigDecimal("100"));
        tx.setType("TDS");
        request.setTransactionHistory(java.util.Collections.singletonList(tx));
        
        when(form90cService.saveForm(any(), any())).thenThrow(new RuntimeException("Error saving form"));

        mockMvc.perform(post("/api/forms/90c")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testSubmit_Http400_ValidationFailed() throws Exception {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        // formId is null

        mockMvc.perform(post("/api/submissions")
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("test@test.com", "password", java.util.Collections.emptyList()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
