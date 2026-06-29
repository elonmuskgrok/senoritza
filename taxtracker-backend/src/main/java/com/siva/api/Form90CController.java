package com.siva.api;

import com.siva.dto.Form90CRequestDTO;
import com.siva.dto.SubmissionRequestDTO;
import com.siva.dto.UploadRequestDTO;
import com.siva.dto.Form90CResponseDTO;
import com.siva.service.Form90CService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Form90CController {

    private final Form90CService form90cService;

    @PostMapping("/api/forms/90c/draft")
    public ResponseEntity<Map<String, Object>> saveDraft(
            @RequestBody Form90CRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.saveDraft(email, request));
    }

    @PostMapping("/api/forms/90c")
    public ResponseEntity<Map<String, Object>> saveForm(
            @Valid @RequestBody Form90CRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.saveForm(email, request));
    }

    @GetMapping("/api/forms/90c")
    public ResponseEntity<Form90CResponseDTO> getForm(
            @RequestParam String financialYear,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.getForm(email, financialYear));
    }

    @PostMapping("/api/uploads")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @Valid @RequestBody UploadRequestDTO request,
            Authentication authentication) throws Exception {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.uploadDocument(email, request));
    }

    @PostMapping("/api/submissions")
    public ResponseEntity<Map<String, Object>> submitForm(
            @Valid @RequestBody SubmissionRequestDTO request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.submitForm(email, request));
    }
}
