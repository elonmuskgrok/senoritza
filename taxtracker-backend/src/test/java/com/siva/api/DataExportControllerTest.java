package com.siva.api;

import com.siva.dto.TransactionResponseDTO;
import com.siva.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

class DataExportControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private DataExportController dataExportController;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authentication = new UsernamePasswordAuthenticationToken("test@test.com", "password");
    }

    private List<TransactionResponseDTO> getMockTransactions() {
        TransactionResponseDTO dto = TransactionResponseDTO.builder()
                .id(1L)
                .transactionDate(LocalDate.now())
                .amount(BigDecimal.valueOf(1000))
                .taxAmount(BigDecimal.valueOf(100))
                .type("TDS")
                .organizationName("Test Org, Inc.") // With comma to test CSV escaping
                .financialYear("2023-2024")
                .build();
        return Collections.singletonList(dto);
    }

    @Test
    void downloadTransactions_invalidFormat_returnsBadRequest() throws Exception {
        ResponseEntity<byte[]> response = dataExportController.downloadTransactions(
                "XML", null, null, null, null, authentication);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported format", new String(response.getBody()));
    }

    @Test
    void downloadTransactions_jsonFormat_returnsJson() throws Exception {
        when(transactionService.getAllTransactions(eq("test@test.com"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(getMockTransactions());

        ResponseEntity<byte[]> response = dataExportController.downloadTransactions(
                "JSON", null, null, null, null, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(new String(response.getBody()).contains("totalRecords"));
        assertEquals("application/json", response.getHeaders().getContentType().toString());
    }

    @Test
    void downloadTransactions_csvFormat_returnsCsv() throws Exception {
        when(transactionService.getAllTransactions(eq("test@test.com"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(getMockTransactions());

        ResponseEntity<byte[]> response = dataExportController.downloadTransactions(
                "CSV", null, null, null, null, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        String csvContent = new String(response.getBody());
        assertTrue(csvContent.contains("Test Org  Inc.")); // Escaped comma
        assertEquals("text/csv", response.getHeaders().getContentType().toString());
    }

    @Test
    void downloadTransactions_pdfFormat_returnsPdf() throws Exception {
        when(transactionService.getAllTransactions(eq("test@test.com"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(getMockTransactions());

        ResponseEntity<byte[]> response = dataExportController.downloadTransactions(
                "PDF", null, null, null, null, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
    }

    @Test
    void downloadTransactions_xlsxFormat_returnsXlsx() throws Exception {
        when(transactionService.getAllTransactions(eq("test@test.com"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(getMockTransactions());

        ResponseEntity<byte[]> response = dataExportController.downloadTransactions(
                "XLSX", null, null, null, null, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertTrue(response.getHeaders().getContentType().toString().contains("spreadsheetml"));
    }
}
