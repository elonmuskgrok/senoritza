package com.taxtracker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull(message = "Please provide a valid formId")
    private Long formId;
}
