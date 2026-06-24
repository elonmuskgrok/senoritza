package com.taxtracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadRequest {
    @NotNull(message = "Please provide a valid formId")
    private Long formId;

    @NotBlank(message = "Please provide a valid name")
    private String name;

    @NotBlank(message = "Please provide valid data")
    private String data;
}
