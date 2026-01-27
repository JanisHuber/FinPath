package ch.finpath.api.dto;

import ch.finpath.persistence.enums.GoalCategory;
import ch.finpath.persistence.enums.GoalStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateGoalRequest(
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        String description,

        @DecimalMin(value = "0.01", message = "Target amount must be positive")
        BigDecimal targetAmount,

        @DecimalMin(value = "0", message = "Current amount cannot be negative")
        BigDecimal currentAmount,

        LocalDate deadline,

        GoalCategory category,

        GoalStatus status,

        Integer priority,

        String icon,

        String color,

        UUID linkedAccountId
) {}
