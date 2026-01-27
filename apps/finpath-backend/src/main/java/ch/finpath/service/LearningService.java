package ch.finpath.service;

import ch.finpath.api.dto.*;
import ch.finpath.persistence.enums.LearningCategory;
import ch.finpath.persistence.enums.LearningStatus;
import ch.finpath.persistence.learning.LearningModuleEntity;
import ch.finpath.persistence.learning.LearningModuleRepository;
import ch.finpath.persistence.learning.LearningProgressEntity;
import ch.finpath.persistence.learning.LearningProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final LearningModuleRepository moduleRepository;
    private final LearningProgressRepository progressRepository;

    public LearningService(
            LearningModuleRepository moduleRepository,
            LearningProgressRepository progressRepository) {
        this.moduleRepository = moduleRepository;
        this.progressRepository = progressRepository;
    }

    // ==================== Module Operations ====================

    public List<LearningModuleSummaryDto> getAllModules(UUID userId, LearningCategory category) {
        List<LearningModuleEntity> modules;

        if (category != null) {
            modules = moduleRepository.findByCategoryAndIsPublishedOrderByDisplayOrder(category, true);
        } else {
            modules = moduleRepository.findByIsPublishedOrderByDisplayOrder(true);
        }

        // Get user progress for all modules
        Map<UUID, LearningProgressEntity> progressMap = getUserProgressMap(userId);

        return modules.stream()
                .map(module -> mapToSummaryDto(module, progressMap.get(module.getId())))
                .collect(Collectors.toList());
    }

    public LearningModuleDto getModule(UUID userId, UUID moduleId) {
        LearningModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        if (!module.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found");
        }

        // Get or create user progress
        LearningProgressEntity progress = progressRepository.findByUserIdAndModuleId(userId, moduleId)
                .orElse(null);

        // Mark as accessed if progress exists
        if (progress != null) {
            progress.markAsAccessed();
            progressRepository.save(progress);
        }

        return mapToDto(module, progress);
    }

    // ==================== Progress Operations ====================

    public List<LearningProgressDto> getUserProgress(UUID userId) {
        return progressRepository.findByUserId(userId).stream()
                .map(this::mapProgressToDto)
                .collect(Collectors.toList());
    }

    public List<LearningProgressDto> getUserProgressByStatus(UUID userId, LearningStatus status) {
        return progressRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::mapProgressToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LearningProgressDto updateProgress(UUID userId, UUID moduleId, UpdateProgressRequest request) {
        // Verify module exists and is published
        LearningModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        if (!module.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found");
        }

        // Get or create progress
        LearningProgressEntity progress = progressRepository.findByUserIdAndModuleId(userId, moduleId)
                .orElseGet(() -> {
                    LearningProgressEntity newProgress = new LearningProgressEntity(userId, moduleId);
                    return newProgress;
                });

        // Update progress fields
        if (request.status() != null) {
            progress.setStatus(request.status());
        }
        if (request.progressPercent() != null) {
            progress.setProgressPercent(request.progressPercent());
            // Auto-complete if 100%
            if (request.progressPercent() >= 100 && progress.getStatus() != LearningStatus.completed) {
                progress.setStatus(LearningStatus.completed);
            }
        }
        if (request.notes() != null) {
            progress.setNotes(request.notes());
        }

        progress.markAsAccessed();
        LearningProgressEntity saved = progressRepository.save(progress);
        return mapProgressToDto(saved);
    }

    @Transactional
    public LearningProgressDto markModuleCompleted(UUID userId, UUID moduleId) {
        UpdateProgressRequest request = new UpdateProgressRequest(LearningStatus.completed, 100, null);
        return updateProgress(userId, moduleId, request);
    }

    @Transactional
    public LearningProgressDto startModule(UUID userId, UUID moduleId) {
        // Verify module exists and is published
        LearningModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));

        if (!module.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found");
        }

        // Get or create progress
        LearningProgressEntity progress = progressRepository.findByUserIdAndModuleId(userId, moduleId)
                .orElseGet(() -> new LearningProgressEntity(userId, moduleId));

        if (progress.getStatus() == LearningStatus.not_started) {
            progress.setStatus(LearningStatus.in_progress);
            progress.setStartedAt(OffsetDateTime.now());
        }
        progress.markAsAccessed();

        LearningProgressEntity saved = progressRepository.save(progress);
        return mapProgressToDto(saved);
    }

    // ==================== Stats Operations ====================

    public LearningStatsDto getUserStats(UUID userId) {
        long totalModules = moduleRepository.findByIsPublishedOrderByDisplayOrder(true).size();
        long completedModules = progressRepository.countCompletedByUserId(userId);

        List<LearningProgressEntity> allProgress = progressRepository.findByUserId(userId);
        long inProgressModules = allProgress.stream()
                .filter(p -> p.getStatus() == LearningStatus.in_progress)
                .count();

        Double avgProgress = progressRepository.getAverageProgressByUserId(userId);
        double averageProgress = avgProgress != null ? avgProgress : 0.0;

        return new LearningStatsDto(totalModules, completedModules, inProgressModules, averageProgress);
    }

    // ==================== Helper Methods ====================

    private Map<UUID, LearningProgressEntity> getUserProgressMap(UUID userId) {
        return progressRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(LearningProgressEntity::getModuleId, p -> p));
    }

    private LearningModuleSummaryDto mapToSummaryDto(LearningModuleEntity module, LearningProgressEntity progress) {
        LearningStatus userStatus = progress != null ? progress.getStatus() : LearningStatus.not_started;
        int userProgress = progress != null ? progress.getProgressPercent() : 0;

        return new LearningModuleSummaryDto(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getSummary(),
                module.getCategory(),
                module.getDifficultyLevel(),
                module.getEstimatedMinutes(),
                module.getTags() != null ? Arrays.asList(module.getTags()) : List.of(),
                userStatus,
                userProgress
        );
    }

    private LearningModuleDto mapToDto(LearningModuleEntity module, LearningProgressEntity progress) {
        LearningProgressDto progressDto = progress != null ? mapProgressToDto(progress) : null;

        return new LearningModuleDto(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getContent(),
                module.getSummary(),
                module.getCategory(),
                module.getDifficultyLevel(),
                module.getEstimatedMinutes(),
                module.getDisplayOrder(),
                module.getTags() != null ? Arrays.asList(module.getTags()) : List.of(),
                progressDto
        );
    }

    private LearningProgressDto mapProgressToDto(LearningProgressEntity progress) {
        return new LearningProgressDto(
                progress.getId(),
                progress.getModuleId(),
                progress.getStatus(),
                progress.getProgressPercent(),
                progress.getStartedAt(),
                progress.getCompletedAt(),
                progress.getLastAccessedAt(),
                progress.getNotes()
        );
    }
}
