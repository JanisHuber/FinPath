package ch.finpath.api.dto;

import ch.finpath.persistence.enums.LearningStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateProgressRequest(
        LearningStatus status,

        @Min(value = 0, message = "Progress must be at least 0")
        @Max(value = 100, message = "Progress cannot exceed 100")
        Integer progressPercent,

        String notes
) {}
