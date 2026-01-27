package ch.finpath.api.dto;

import ch.finpath.persistence.enums.LearningCategory;

import java.util.List;
import java.util.UUID;

public record LearningModuleDto(
        UUID id,
        String title,
        String description,
        String content,
        String summary,
        LearningCategory category,
        int difficultyLevel,
        int estimatedMinutes,
        int displayOrder,
        List<String> tags,
        LearningProgressDto userProgress
) {}
