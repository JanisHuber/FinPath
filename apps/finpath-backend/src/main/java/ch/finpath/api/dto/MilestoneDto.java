package ch.finpath.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MilestoneDto(
        UUID id,
        String name,
        String description,
        BigDecimal targetAmount,
        LocalDate targetDate,
        boolean isAchieved,
        OffsetDateTime achievedAt,
        int displayOrder
) {}
