package com.taxtracker.service;

import com.taxtracker.dto.request.Form90cRequest;
import com.taxtracker.dto.request.SubmissionRequest;
import com.taxtracker.dto.response.Form90cResponse;
import com.taxtracker.entity.Form90c;
import com.taxtracker.entity.Submission;
import com.taxtracker.entity.User;
import com.taxtracker.repository.DocumentRepository;
import com.taxtracker.repository.Form90cRepository;
import com.taxtracker.repository.Form90cTransactionHistoryRepository;
import com.taxtracker.repository.SubmissionRepository;
import com.taxtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Form90cServiceTest {

    @Mock private Form90cRepository form90cRepository;
    @Mock private Form90cTransactionHistoryRepository historyRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private Form90cService form90cService;

    @Test
    void testSaveDraft_Success() {
        Form90cRequest request = new Form90cRequest();
        request.setFinancialYear("2023-2024");
        request.setTransactionHistory(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90c form = new Form90c();
        form.setId(10L);
        when(form90cRepository.findByUserEmailAndStatus("test@test.com", "DRAFT")).thenReturn(Optional.of(form));
        when(form90cRepository.save(any(Form90c.class))).thenReturn(form);

        Map<String, Object> response = form90cService.saveDraft("test@test.com", request);

        assertNotNull(response);
        assertEquals(10L, response.get("formId"));
        assertEquals("DRAFT", response.get("status"));
    }

    @Test
    void testSaveDraft_InvalidFinancialYear() {
        Form90cRequest request = new Form90cRequest();
        request.setFinancialYear("2023-2025"); // Gap is 2 years

        RuntimeException ex = assertThrows(RuntimeException.class, () -> form90cService.saveDraft("test@test.com", request));
        assertTrue(ex.getMessage().contains("1-year gap"));
    }

    @Test
    void testGetForm_Success() {
        Form90c form = new Form90c();
        form.setId(1L);
        form.setStatus("DRAFT");
        
        when(form90cRepository.findByUserEmailAndStatus("test@test.com", "DRAFT")).thenReturn(Optional.of(form));
        when(historyRepository.findByFormId(1L)).thenReturn(List.of());

        Form90cResponse response = form90cService.getForm("test@test.com", "test@test.com");

        assertNotNull(response);
        assertEquals(1L, response.getFormId());
    }

    @Test
    void testGetForm_Unauthorized() {
        assertThrows(RuntimeException.class, () -> form90cService.getForm("test@test.com", "other@test.com"));
    }

    @Test
    void testSubmitForm_Success() {
        SubmissionRequest request = new SubmissionRequest();
        request.setFormId(10L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90c form = new Form90c();
        form.setId(10L);
        form.setStatus("DRAFT");
        when(form90cRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(form));

        when(submissionRepository.existsByFormId(10L)).thenReturn(false);
        when(historyRepository.existsByFormId(10L)).thenReturn(true);
        when(documentRepository.existsByFormId(10L)).thenReturn(true);

        Submission submission = new Submission();
        submission.setId(5L);
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Map<String, Object> response = form90cService.submitForm("test@test.com", request);

        assertEquals(5L, response.get("submissionId"));
    }
}
