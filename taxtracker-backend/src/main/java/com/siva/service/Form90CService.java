package com.siva.service;

import com.siva.dto.Form90CRequestDTO;
import com.siva.dto.Form90CDraftRequestDTO;
import com.siva.dto.SubmissionRequestDTO;
import com.siva.dto.UploadRequestDTO;
import com.siva.dto.Form90CResponseDTO;
import com.siva.entity.*;
import com.siva.repository.*;
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
public class Form90CService {

    private final Form90CRepository form90cRepository;
    private final Form90CTransactionHistoryRepository historyRepository;
    private final DocumentRepository documentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> saveDraft(String email, Form90CDraftRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90C form = form90cRepository.findByUserEmailAndFinancialYear(email, request.getFinancialYear()).orElse(new Form90C());
        form.setUser(user);
        form.setName(request.getName());
        form.setMobileNumber(request.getMobileNumber());
        form.setFinancialYear(request.getFinancialYear());
        form.setStatus("DRAFT");
        
        if (form.getId() != null) {
            historyRepository.deleteByFormId(form.getId());
        }
        
        Form90C savedForm = form90cRepository.save(form);

        if (request.getTransactionHistory() != null) {
            List<Form90CTransactionHistory> historyList = request.getTransactionHistory().stream()
                    .map(txn -> Form90CTransactionHistory.builder()
                            .form(savedForm)
                            .organizationName(txn.getOrganizationName())
                            .amount(txn.getAmount())
                            .taxAmount(txn.getTaxAmount())
                            .type(txn.getType())
                            .build())
                    .collect(Collectors.toList());

            historyRepository.saveAll(historyList);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("formId", savedForm.getId());
        response.put("status", "DRAFT");
        response.put("message", "Form 90C draft saved successfully.");
        return response;
    }

    @Transactional
    public Map<String, Object> saveForm(String email, Form90CRequestDTO request) {
        if (request.getFinancialYear() == null || !request.getFinancialYear().matches("^\\d{4}-\\d{4}$")) {
            throw new RuntimeException("Invalid Financial Year format. Use YYYY-YYYY");
        }
        String[] years = request.getFinancialYear().split("-");
        if (Integer.parseInt(years[1]) - Integer.parseInt(years[0]) != 1) {
            throw new RuntimeException("Invalid Financial Year. Must be a 1-year gap (e.g., 2022-2023)");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90C form = form90cRepository.findByUserEmailAndFinancialYear(email, request.getFinancialYear()).orElse(new Form90C());
        form.setUser(user);
        form.setName(request.getName());
        form.setMobileNumber(request.getMobileNumber());
        form.setFinancialYear(request.getFinancialYear());
        form.setStatus("DRAFT");
        
        if (form.getId() != null) {
            historyRepository.deleteByFormId(form.getId());
        }
        
        Form90C savedForm = form90cRepository.save(form);

        List<Form90CTransactionHistory> historyList = request.getTransactionHistory().stream()
                .map(txn -> Form90CTransactionHistory.builder()
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
        response.put("message", "Form 90C data saved successfully.");
        return response;
    }

    public Form90CResponseDTO getForm(String email, String financialYear) {
        Form90C form = form90cRepository.findByUserEmailAndFinancialYear(email, financialYear)
                .orElseThrow(() -> new RuntimeException("Form not found for this user and financial year"));

        List<Form90CTransactionHistory> historyList = historyRepository.findByFormId(form.getId());

        List<Form90CResponseDTO.TransactionHistoryResponse> historyResponse = historyList.stream()
                .map(h -> Form90CResponseDTO.TransactionHistoryResponse.builder()
                        .organizationName(h.getOrganizationName())
                        .amount(h.getAmount())
                        .taxAmount(h.getTaxAmount())
                        .type(h.getType())
                        .build())
                .collect(Collectors.toList());

        return Form90CResponseDTO.builder()
                .formId(form.getId())
                .name(form.getName())
                .mobileNumber(form.getMobileNumber())
                .financialYear(form.getFinancialYear())
                .status(form.getStatus())
                .transactionHistory(historyResponse)
                .build();
    }

    @Transactional
    public Map<String, Object> uploadDocument(String email, UploadRequestDTO request) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90C form = form90cRepository.findByIdAndUserId(request.getFormId(), user.getId())
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
    public Map<String, Object> submitForm(String email, SubmissionRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form90C form = form90cRepository.findByIdAndUserId(request.getFormId(), user.getId())
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
