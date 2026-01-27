package ch.finpath.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateMilestoneRequest(
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        String description,

        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        LocalDate targetDate,

        Boolean isAchieved,

        Integer displayOrder
) {}
