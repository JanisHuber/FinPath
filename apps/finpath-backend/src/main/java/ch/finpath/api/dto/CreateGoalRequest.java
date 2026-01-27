package ch.finpath.api.dto;

import ch.finpath.persistence.enums.GoalCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateGoalRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        String description,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        BigDecimal initialAmount,

        LocalDate deadline,

        @NotNull(message = "Category is required")
        GoalCategory category,

        Integer priority,

        String icon,

        String color,

        UUID linkedAccountId
) {}
