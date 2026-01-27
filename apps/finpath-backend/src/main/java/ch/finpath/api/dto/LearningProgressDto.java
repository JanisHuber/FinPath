package ch.finpath.api.dto;

import ch.finpath.persistence.enums.LearningStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LearningProgressDto(
        UUID id,
        UUID moduleId,
        LearningStatus status,
        int progressPercent,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        OffsetDateTime lastAccessedAt,
        String notes
) {}
