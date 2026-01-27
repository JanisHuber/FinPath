package ch.finpath.api.dto;

import ch.finpath.persistence.enums.GoalCategory;
import ch.finpath.persistence.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FinancialGoalDto(
        UUID id,
        String name,
        String description,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        GoalCategory category,
        GoalStatus status,
        int priority,
        String icon,
        String color,
        UUID linkedAccountId,
        double progressPercent,
        LocalDate estimatedCompletionDate,
        List<MilestoneDto> milestones
) {}
