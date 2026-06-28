package com.siva.service;

import com.siva.dto.Form90CRequestDTO;
import com.siva.dto.SubmissionRequestDTO;
import com.siva.dto.Form90CResponseDTO;
import com.siva.entity.Form90C;
import com.siva.entity.Submission;
import com.siva.entity.User;
import com.siva.repository.DocumentRepository;
import com.siva.repository.Form90CRepository;
import com.siva.repository.Form90CTransactionHistoryRepository;
import com.siva.repository.SubmissionRepository;
import com.siva.repository.UserRepository;
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
public class Form90CServiceTest {

    @Mock private Form90CRepository form90cRepository;
    @Mock private Form90CTransactionHistoryRepository historyRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private Form90CService form90cService;

    @Test
    void testSaveDraft_Success() {
        com.siva.dto.Form90CDraftRequestDTO request = new com.siva.dto.Form90CDraftRequestDTO();
        request.setFinancialYear("2023-2024");
        request.setTransactionHistory(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(10L);
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024")).thenReturn(Optional.of(form));
        when(form90cRepository.save(any(Form90C.class))).thenReturn(form);

        Map<String, Object> response = form90cService.saveDraft("test@test.com", request);

        assertNotNull(response);
        assertEquals(10L, response.get("formId"));
        assertEquals("DRAFT", response.get("status"));
    }

    @Test
    void testSaveDraft_InvalidFinancialYear() {
        Form90CRequestDTO request = new Form90CRequestDTO();
        request.setFinancialYear("2023-2025"); // Gap is 2 years

        RuntimeException ex = assertThrows(RuntimeException.class, () -> form90cService.saveForm("test@test.com", request));
        assertTrue(ex.getMessage().contains("1-year gap"));
    }

    @Test
    void testGetForm_Success() {
        Form90C form = new Form90C();
        form.setId(1L);
        form.setStatus("DRAFT");
        
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024")).thenReturn(Optional.of(form));
        when(historyRepository.findByFormId(1L)).thenReturn(List.of());

        Form90CResponseDTO response = form90cService.getForm("test@test.com", "2023-2024");

        assertNotNull(response);
        assertEquals(1L, response.getFormId());
    }

    @Test
    void testGetForm_NotFound() {
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> form90cService.getForm("test@test.com", "2023-2024"));
    }

    @Test
    void testSubmitForm_Success() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setFormId(10L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
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

    @Test
    void testSaveForm_InvalidFormat() {
        Form90CRequestDTO request = new Form90CRequestDTO();
        request.setFinancialYear("20232024"); // no hyphen
        assertThrows(RuntimeException.class, () -> form90cService.saveForm("test@test.com", request));
    }

    @Test
    void testSaveForm_Success() {
        Form90CRequestDTO request = new Form90CRequestDTO();
        request.setFinancialYear("2023-2024");
        request.setTransactionHistory(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        when(form90cRepository.findByUserEmailAndFinancialYear("test@test.com", "2023-2024")).thenReturn(Optional.of(form));
        when(form90cRepository.save(any(Form90C.class))).thenReturn(form);

        Map<String, Object> response = form90cService.saveForm("test@test.com", request);
        assertNotNull(response);
    }

    @Test
    void testSubmitForm_AlreadySubmitted() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setFormId(10L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(10L);
        form.setStatus("SUBMITTED");
        when(form90cRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(form));

        assertThrows(RuntimeException.class, () -> form90cService.submitForm("test@test.com", request));
    }

    @Test
    void testSubmitForm_NoTransactions() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setFormId(10L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(10L);
        form.setStatus("DRAFT");
        when(form90cRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(form));
        when(submissionRepository.existsByFormId(10L)).thenReturn(false);
        when(historyRepository.existsByFormId(10L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> form90cService.submitForm("test@test.com", request));
        assertTrue(ex.getMessage().contains("No transactions added"));
    }

    @Test
    void testSubmitForm_NoDocuments() {
        SubmissionRequestDTO request = new SubmissionRequestDTO();
        request.setFormId(10L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(10L);
        form.setStatus("DRAFT");
        when(form90cRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(form));
        when(submissionRepository.existsByFormId(10L)).thenReturn(false);
        when(historyRepository.existsByFormId(10L)).thenReturn(true);
        when(documentRepository.existsByFormId(10L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> form90cService.submitForm("test@test.com", request));
        assertTrue(ex.getMessage().contains("Mandatory File not uploaded"));
    }

    @Test
    void testUploadDocument_InvalidMagicBytes() throws Exception {
        com.siva.dto.UploadRequestDTO request = new com.siva.dto.UploadRequestDTO();
        request.setFormId(1L);
        request.setName("test.txt");
        // Base64 for "Hello"
        request.setData("SGVsbG8=");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(1L);
        when(form90cRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(form));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> form90cService.uploadDocument("test@test.com", request));
        assertTrue(ex.getMessage().contains("Invalid file format"));
    }

    @Test
    void testUploadDocument_SuccessPDF() throws Exception {
        com.siva.dto.UploadRequestDTO request = new com.siva.dto.UploadRequestDTO();
        request.setFormId(1L);
        request.setName("test.pdf");
        // Base64 for "%PDF" which are PDF magic bytes
        request.setData("JVBERg==");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Form90C form = new Form90C();
        form.setId(1L);
        when(form90cRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(form));

        com.siva.entity.Document savedDoc = new com.siva.entity.Document();
        savedDoc.setId(55L);
        when(documentRepository.save(any())).thenReturn(savedDoc);

        Map<String, Object> response = form90cService.uploadDocument("test@test.com", request);
        assertEquals(55L, response.get("documentId"));
    }
}
