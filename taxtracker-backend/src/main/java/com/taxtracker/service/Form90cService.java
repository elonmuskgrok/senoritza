package com.taxtracker.service;

import com.taxtracker.dto.request.Form90cRequest;
import com.taxtracker.dto.request.SubmissionRequest;
import com.taxtracker.dto.request.UploadRequest;
import com.taxtracker.dto.response.Form90cResponse;
import com.taxtracker.entity.*;
import com.taxtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Form90cService {

    private final Form90cRepository form90cRepository;
    private final Form90cTransactionHistoryRepository historyRepository;
    private final DocumentRepository documentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> saveDraft(String email, Form90cRequest request) {
        if (request.getFinancialYear() == null || !request.getFinancialYear().matches("^\\d{4}-\\d{4}$")) {
            throw new RuntimeException("Invalid Financial Year format. Use YYYY-YYYY");
        }
        String[] years = request.getFinancialYear().split("-");
        if (Integer.parseInt(years[1]) - Integer.parseInt(years[0]) != 1) {
            throw new RuntimeException("Invalid Financial Year. Must be a 1-year gap (e.g., 2022-2023)");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90c form = form90cRepository.findByUserEmailAndStatus(email, "DRAFT").orElse(new Form90c());
        form.setUser(user);
        form.setName(request.getName());
        form.setMobileNumber(request.getMobileNumber());
        form.setFinancialYear(request.getFinancialYear());
        form.setStatus("DRAFT");
        
        if (form.getId() != null) {
            historyRepository.deleteByFormId(form.getId());
        }
        
        Form90c savedForm = form90cRepository.save(form);

        List<Form90cTransactionHistory> historyList = request.getTransactionHistory().stream()
                .map(txn -> Form90cTransactionHistory.builder()
                        .form(savedForm)
                        .organizationName(txn.getOrganizationName())
                        .amount(txn.getAmount())
                        .taxAmount(txn.getTaxAmount())
                        .type(txn.getType())
                        .build())
                .collect(Collectors.toList());

        historyRepository.saveAll(historyList);

        Map<String, Object> response = new HashMap<>();
        response.put("formId", savedForm.getId());
        response.put("status", "DRAFT");
        response.put("message", "Form 90C data submitted successfully.");
        return response;
    }

    public Form90cResponse getForm(String email, String pathEmail) {
        if (!email.equals(pathEmail)) {
            throw new RuntimeException("You are not authorized to access this resource.");
        }

        Form90c form = form90cRepository.findByUserEmailAndStatus(email, "DRAFT")
                .orElseThrow(() -> new RuntimeException("Form not found for this user"));

        List<Form90cTransactionHistory> historyList = historyRepository.findByFormId(form.getId());

        List<Form90cResponse.TransactionHistoryResponse> historyResponse = historyList.stream()
                .map(h -> Form90cResponse.TransactionHistoryResponse.builder()
                        .organizationName(h.getOrganizationName())
                        .amount(h.getAmount())
                        .taxAmount(h.getTaxAmount())
                        .type(h.getType())
                        .build())
                .collect(Collectors.toList());

        return Form90cResponse.builder()
                .formId(form.getId())
                .name(form.getName())
                .mobileNumber(form.getMobileNumber())
                .financialYear(form.getFinancialYear())
                .status(form.getStatus())
                .transactionHistory(historyResponse)
                .build();
    }

    @Transactional
    public Map<String, Object> uploadDocument(String email, UploadRequest request) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90c form = form90cRepository.findByIdAndUserId(request.getFormId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Form not found or you are not authorized to access this resource."));

        byte[] decodedBytes = Base64.getDecoder().decode(request.getData());

        if (decodedBytes.length > 2097152) {
            throw new RuntimeException("File size exceeds limit");
        }

        // Magic bytes check
        String fileType = detectFileType(decodedBytes);
        if (fileType == null) {
            throw new RuntimeException("Invalid file format");
        }

        String fileName = UUID.randomUUID().toString() + "_" + request.getName();
        String uploadDir = "uploads/" + form.getId() + "/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        File file = new File(uploadDir + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decodedBytes);
        }

        Document document = Document.builder()
                .form(form)
                .fileName(fileName)
                .fileType(fileType)
                .fileSizeBytes(decodedBytes.length)
                .storagePath(file.getAbsolutePath())
                .build();

        Document saved = documentRepository.save(document);

        Map<String, Object> response = new HashMap<>();
        response.put("documentId", saved.getId());
        response.put("message", "File uploaded successfully.");
        return response;
    }

    @Transactional
    public Map<String, Object> submitForm(String email, SubmissionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90c form = form90cRepository.findByIdAndUserId(request.getFormId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Form not found or you are not authorized to access this resource."));

        if ("SUBMITTED".equals(form.getStatus())) {
            throw new RuntimeException("Form already submitted");
        }

        if (submissionRepository.existsByFormId(form.getId())) {
            throw new RuntimeException("Submission already exists for this form");
        }

        boolean hasTransactions = historyRepository.existsByFormId(form.getId());
        if (!hasTransactions) {
            throw new RuntimeException("No transactions added.");
        }

        boolean hasDocuments = documentRepository.existsByFormId(form.getId());
        if (!hasDocuments) {
            throw new RuntimeException("Mandatory File not uploaded");
        }

        form.setStatus("SUBMITTED");
        form.setSubmittedAt(LocalDateTime.now());
        form90cRepository.save(form);

        Submission submission = Submission.builder()
                .form(form)
                .status("CONFIRMED")
                .confirmationMessage("Form 90C submitted successfully")
                .build();
        
        Submission saved = submissionRepository.save(submission);

        Map<String, Object> response = new HashMap<>();
        response.put("submissionId", saved.getId());
        response.put("message", "Form submitted successfully.");
        return response;
    }

    private String detectFileType(byte[] data) {
        if (data.length < 4) return null;
        if (data[0] == (byte) 0x25 && data[1] == (byte) 0x50 && data[2] == (byte) 0x44 && data[3] == (byte) 0x46) {
            return "PDF";
        }
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return "JPEG";
        }
        return null;
    }
}
