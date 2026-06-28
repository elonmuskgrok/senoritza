package com.siva.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequestDTO {
    @NotNull(message = "Please provide a valid formId")
    private Long formId;
}
