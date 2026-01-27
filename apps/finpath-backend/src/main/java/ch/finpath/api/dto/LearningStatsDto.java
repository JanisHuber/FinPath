package ch.finpath.api.dto;

public record LearningStatsDto(
        long totalModules,
        long completedModules,
        long inProgressModules,
        double averageProgress
) {}
