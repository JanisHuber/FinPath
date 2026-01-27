package ch.finpath.api.dto;

import ch.finpath.persistence.enums.LearningCategory;
import ch.finpath.persistence.enums.LearningStatus;

import java.util.List;
import java.util.UUID;

public record LearningModuleSummaryDto(
        UUID id,
        String title,
        String description,
        String summary,
        LearningCategory category,
        int difficultyLevel,
        int estimatedMinutes,
        List<String> tags,
        LearningStatus userStatus,
        int userProgress
) {}
