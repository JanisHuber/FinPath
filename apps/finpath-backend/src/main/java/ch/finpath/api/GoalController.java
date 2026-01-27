package ch.finpath.api;

import ch.finpath.api.dto.*;
import ch.finpath.persistence.enums.GoalCategory;
import ch.finpath.persistence.enums.GoalStatus;
import ch.finpath.security.AuthenticatedUser;
import ch.finpath.service.FinancialGoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final FinancialGoalService goalService;

    public GoalController(FinancialGoalService goalService) {
        this.goalService = goalService;
    }

    // ==================== Goal Endpoints ====================

    @GetMapping
    public List<FinancialGoalDto> getGoals(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) GoalStatus status,
            @RequestParam(required = false) GoalCategory category) {
        return goalService.getGoals(user.id(), status, category);
    }

    @GetMapping("/{id}")
    public FinancialGoalDto getGoal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return goalService.getGoal(user.id(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialGoalDto createGoal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateGoalRequest request) {
        return goalService.createGoal(user.id(), request);
    }

    @PutMapping("/{id}")
    public FinancialGoalDto updateGoal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGoalRequest request) {
        return goalService.updateGoal(user.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        goalService.deleteGoal(user.id(), id);
    }

    @PostMapping("/{id}/contribute")
    public FinancialGoalDto addContribution(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody ContributionRequest request) {
        return goalService.addContribution(user.id(), id, request.amount());
    }

    // ==================== Milestone Endpoints ====================

    @GetMapping("/{goalId}/milestones")
    public List<MilestoneDto> getMilestones(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId) {
        return goalService.getMilestones(user.id(), goalId);
    }

    @GetMapping("/{goalId}/milestones/{milestoneId}")
    public MilestoneDto getMilestone(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId,
            @PathVariable UUID milestoneId) {
        return goalService.getMilestone(user.id(), goalId, milestoneId);
    }

    @PostMapping("/{goalId}/milestones")
    @ResponseStatus(HttpStatus.CREATED)
    public MilestoneDto createMilestone(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId,
            @Valid @RequestBody CreateMilestoneRequest request) {
        return goalService.createMilestone(user.id(), goalId, request);
    }

    @PutMapping("/{goalId}/milestones/{milestoneId}")
    public MilestoneDto updateMilestone(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId,
            @PathVariable UUID milestoneId,
            @Valid @RequestBody UpdateMilestoneRequest request) {
        return goalService.updateMilestone(user.id(), goalId, milestoneId, request);
    }

    @PutMapping("/{goalId}/milestones/{milestoneId}/achieved")
    public MilestoneDto markMilestoneAchieved(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId,
            @PathVariable UUID milestoneId,
            @RequestParam boolean achieved) {
        return goalService.markMilestoneAchieved(user.id(), goalId, milestoneId, achieved);
    }

    @DeleteMapping("/{goalId}/milestones/{milestoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMilestone(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID goalId,
            @PathVariable UUID milestoneId) {
        goalService.deleteMilestone(user.id(), goalId, milestoneId);
    }
}
