package com.taxtracker.api;

import com.taxtracker.dto.request.Form90cRequest;
import com.taxtracker.dto.request.SubmissionRequest;
import com.taxtracker.dto.request.UploadRequest;
import com.taxtracker.dto.response.Form90cResponse;
import com.taxtracker.service.Form90cService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Form90cController {

    private final Form90cService form90cService;

    @PostMapping("/api/forms/90c")
    public ResponseEntity<Map<String, Object>> saveDraft(
            @Valid @RequestBody Form90cRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.saveDraft(email, request));
    }

    @GetMapping("/api/forms/90c/{emailId}")
    public ResponseEntity<Form90cResponse> getForm(
            @PathVariable String emailId,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.getForm(email, emailId));
    }

    @PostMapping("/api/uploads")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @Valid @RequestBody UploadRequest request,
            Authentication authentication) throws Exception {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.uploadDocument(email, request));
    }

    @PostMapping("/api/submissions")
    public ResponseEntity<Map<String, Object>> submitForm(
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(form90cService.submitForm(email, request));
    }
}
