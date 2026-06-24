package com.taxtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxtracker.dto.request.Form90cRequest;
import com.taxtracker.dto.request.SubmissionRequest;
import com.taxtracker.service.Form90cService;
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

import com.taxtracker.security.CustomUserDetailsService;
import com.taxtracker.security.JwtTokenProvider;

@WebMvcTest(Form90cController.class)
@AutoConfigureMockMvc(addFilters = false)
public class Form90cControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Form90cService form90cService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com")
    void testSaveDraft_Http200() throws Exception {
        Form90cRequest request = new Form90cRequest();
        request.setFinancialYear("2023-2024");
        request.setName("Test");
        request.setMobileNumber("1234567890");
        request.setTransactionHistory(java.util.Collections.emptyList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("formId", 1);
        
        when(form90cService.saveDraft(any(), any(Form90cRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/form90c/saveDraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testSubmit_Http200() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        request.setFormId(1L);
        
        Map<String, Object> response = new HashMap<>();
        response.put("submissionId", 5);

        when(form90cService.submitForm(any(), any(SubmissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/form90c/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
