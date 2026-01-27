package ch.finpath.api;

import ch.finpath.api.dto.*;
import ch.finpath.persistence.enums.LearningCategory;
import ch.finpath.persistence.enums.LearningStatus;
import ch.finpath.security.AuthenticatedUser;
import ch.finpath.service.LearningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    // ==================== Module Endpoints ====================

    @GetMapping("/modules")
    public List<LearningModuleSummaryDto> getModules(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) LearningCategory category) {
        return learningService.getAllModules(user.id(), category);
    }

    @GetMapping("/modules/{id}")
    public LearningModuleDto getModule(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return learningService.getModule(user.id(), id);
    }

    // ==================== Progress Endpoints ====================

    @GetMapping("/progress")
    public List<LearningProgressDto> getUserProgress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) LearningStatus status) {
        if (status != null) {
            return learningService.getUserProgressByStatus(user.id(), status);
        }
        return learningService.getUserProgress(user.id());
    }

    @PostMapping("/progress/{moduleId}")
    @ResponseStatus(HttpStatus.OK)
    public LearningProgressDto updateProgress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID moduleId,
            @Valid @RequestBody UpdateProgressRequest request) {
        return learningService.updateProgress(user.id(), moduleId, request);
    }

    @PostMapping("/modules/{moduleId}/start")
    public LearningProgressDto startModule(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID moduleId) {
        return learningService.startModule(user.id(), moduleId);
    }

    @PostMapping("/modules/{moduleId}/complete")
    public LearningProgressDto completeModule(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID moduleId) {
        return learningService.markModuleCompleted(user.id(), moduleId);
    }

    // ==================== Stats Endpoints ====================

    @GetMapping("/stats")
    public LearningStatsDto getUserStats(@AuthenticationPrincipal AuthenticatedUser user) {
        return learningService.getUserStats(user.id());
    }
}
