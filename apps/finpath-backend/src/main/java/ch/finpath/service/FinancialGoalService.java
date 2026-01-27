package ch.finpath.service;

import ch.finpath.api.dto.*;
import ch.finpath.persistence.enums.GoalCategory;
import ch.finpath.persistence.enums.GoalStatus;
import ch.finpath.persistence.goals.FinancialGoalEntity;
import ch.finpath.persistence.goals.FinancialGoalRepository;
import ch.finpath.persistence.goals.MilestoneEntity;
import ch.finpath.persistence.goals.MilestoneRepository;
import ch.finpath.persistence.transactions.TransactionEntity;
import ch.finpath.persistence.transactions.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FinancialGoalService {

    private final FinancialGoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;
    private final TransactionRepository transactionRepository;

    public FinancialGoalService(
            FinancialGoalRepository goalRepository,
            MilestoneRepository milestoneRepository,
            TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.milestoneRepository = milestoneRepository;
        this.transactionRepository = transactionRepository;
    }

    // ==================== Goal CRUD Operations ====================

    public List<FinancialGoalDto> getGoals(UUID userId, GoalStatus status, GoalCategory category) {
        List<FinancialGoalEntity> goals;

        if (status != null && category != null) {
            goals = goalRepository.findByUserIdAndStatusAndCategoryOrderByPriorityAsc(userId, status, category);
        } else if (status != null) {
            goals = goalRepository.findByUserIdAndStatusOrderByPriorityAsc(userId, status);
        } else if (category != null) {
            goals = goalRepository.findByUserIdAndCategoryOrderByPriorityAsc(userId, category);
        } else {
            goals = goalRepository.findByUserIdOrderByPriorityAsc(userId);
        }

        return goals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public FinancialGoalDto getGoal(UUID userId, UUID goalId) {
        FinancialGoalEntity entity = findGoalAndVerifyOwnership(userId, goalId);
        return mapToDto(entity);
    }

    @Transactional
    public FinancialGoalDto createGoal(UUID userId, CreateGoalRequest request) {
        FinancialGoalEntity entity = new FinancialGoalEntity();
        entity.setUserId(userId);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setTargetAmount(request.targetAmount());
        entity.setCategory(request.category());

        if (request.initialAmount() != null) {
            entity.setCurrentAmount(request.initialAmount());
        }
        if (request.deadline() != null) {
            entity.setDeadline(request.deadline());
        }
        if (request.priority() != null) {
            entity.setPriority(request.priority());
        } else {
            // Set priority to be after existing goals
            List<FinancialGoalEntity> existingGoals = goalRepository.findByUserIdOrderByPriorityAsc(userId);
            entity.setPriority(existingGoals.size() + 1);
        }
        if (request.icon() != null) {
            entity.setIcon(request.icon());
        }
        if (request.color() != null) {
            entity.setColor(request.color());
        }
        if (request.linkedAccountId() != null) {
            entity.setLinkedAccountId(request.linkedAccountId());
        }

        FinancialGoalEntity saved = goalRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public FinancialGoalDto updateGoal(UUID userId, UUID goalId, UpdateGoalRequest request) {
        FinancialGoalEntity entity = findGoalAndVerifyOwnership(userId, goalId);

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.targetAmount() != null) {
            entity.setTargetAmount(request.targetAmount());
        }
        if (request.currentAmount() != null) {
            entity.setCurrentAmount(request.currentAmount());
            // Auto-complete if target reached
            if (entity.getCurrentAmount().compareTo(entity.getTargetAmount()) >= 0
                    && entity.getStatus() == GoalStatus.active) {
                entity.setStatus(GoalStatus.completed);
            }
        }
        if (request.deadline() != null) {
            entity.setDeadline(request.deadline());
        }
        if (request.category() != null) {
            entity.setCategory(request.category());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        if (request.priority() != null) {
            entity.setPriority(request.priority());
        }
        if (request.icon() != null) {
            entity.setIcon(request.icon());
        }
        if (request.color() != null) {
            entity.setColor(request.color());
        }
        if (request.linkedAccountId() != null) {
            entity.setLinkedAccountId(request.linkedAccountId());
        }

        FinancialGoalEntity saved = goalRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteGoal(UUID userId, UUID goalId) {
        FinancialGoalEntity entity = findGoalAndVerifyOwnership(userId, goalId);
        goalRepository.delete(entity);
    }

    @Transactional
    public FinancialGoalDto addContribution(UUID userId, UUID goalId, BigDecimal amount) {
        FinancialGoalEntity entity = findGoalAndVerifyOwnership(userId, goalId);

        BigDecimal newAmount = entity.getCurrentAmount().add(amount);
        entity.setCurrentAmount(newAmount);

        // Auto-complete if target reached
        if (newAmount.compareTo(entity.getTargetAmount()) >= 0 && entity.getStatus() == GoalStatus.active) {
            entity.setStatus(GoalStatus.completed);
        }

        FinancialGoalEntity saved = goalRepository.save(entity);
        return mapToDto(saved);
    }

    // ==================== Milestone CRUD Operations ====================

    public List<MilestoneDto> getMilestones(UUID userId, UUID goalId) {
        findGoalAndVerifyOwnership(userId, goalId);
        return milestoneRepository.findByGoalIdOrderByDisplayOrder(goalId)
                .stream()
                .map(this::mapMilestoneToDto)
                .collect(Collectors.toList());
    }

    public MilestoneDto getMilestone(UUID userId, UUID goalId, UUID milestoneId) {
        findGoalAndVerifyOwnership(userId, goalId);
        MilestoneEntity milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

        if (!milestone.getGoal().getId().equals(goalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Milestone does not belong to this goal");
        }

        return mapMilestoneToDto(milestone);
    }

    @Transactional
    public MilestoneDto createMilestone(UUID userId, UUID goalId, CreateMilestoneRequest request) {
        FinancialGoalEntity goal = findGoalAndVerifyOwnership(userId, goalId);

        MilestoneEntity entity = new MilestoneEntity();
        entity.setGoal(goal);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setTargetAmount(request.targetAmount());
        entity.setTargetDate(request.targetDate());

        if (request.displayOrder() != null) {
            entity.setDisplayOrder(request.displayOrder());
        } else {
            // Set display order to be after existing milestones
            List<MilestoneEntity> existingMilestones = milestoneRepository.findByGoalIdOrderByDisplayOrder(goalId);
            entity.setDisplayOrder(existingMilestones.size());
        }

        MilestoneEntity saved = milestoneRepository.save(entity);
        return mapMilestoneToDto(saved);
    }

    @Transactional
    public MilestoneDto updateMilestone(UUID userId, UUID goalId, UUID milestoneId, UpdateMilestoneRequest request) {
        findGoalAndVerifyOwnership(userId, goalId);

        MilestoneEntity entity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

        if (!entity.getGoal().getId().equals(goalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Milestone does not belong to this goal");
        }

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.targetAmount() != null) {
            entity.setTargetAmount(request.targetAmount());
        }
        if (request.targetDate() != null) {
            entity.setTargetDate(request.targetDate());
        }
        if (request.isAchieved() != null) {
            entity.setAchieved(request.isAchieved());
        }
        if (request.displayOrder() != null) {
            entity.setDisplayOrder(request.displayOrder());
        }

        MilestoneEntity saved = milestoneRepository.save(entity);
        return mapMilestoneToDto(saved);
    }

    @Transactional
    public MilestoneDto markMilestoneAchieved(UUID userId, UUID goalId, UUID milestoneId, boolean achieved) {
        findGoalAndVerifyOwnership(userId, goalId);

        MilestoneEntity entity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

        if (!entity.getGoal().getId().equals(goalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Milestone does not belong to this goal");
        }

        entity.setAchieved(achieved);
        if (achieved) {
            entity.setAchievedAt(OffsetDateTime.now());
        } else {
            entity.setAchievedAt(null);
        }

        MilestoneEntity saved = milestoneRepository.save(entity);
        return mapMilestoneToDto(saved);
    }

    @Transactional
    public void deleteMilestone(UUID userId, UUID goalId, UUID milestoneId) {
        findGoalAndVerifyOwnership(userId, goalId);

        MilestoneEntity entity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

        if (!entity.getGoal().getId().equals(goalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Milestone does not belong to this goal");
        }

        milestoneRepository.delete(entity);
    }

    // ==================== Forecast Algorithm ====================

    /**
     * Calculates the estimated completion date for a goal based on historical contributions.
     * Uses the average monthly savings rate from the past 6 months to project when the target will be reached.
     */
    public LocalDate calculateEstimatedCompletionDate(FinancialGoalEntity goal) {
        if (goal.getStatus() == GoalStatus.completed) {
            return LocalDate.now();
        }

        BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount());
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return LocalDate.now();
        }

        // Calculate average monthly contribution from transactions in the last 6 months
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        BigDecimal monthlyContribution = calculateAverageMonthlyContribution(goal.getUserId(), goal.getLinkedAccountId(), sixMonthsAgo);

        if (monthlyContribution.compareTo(BigDecimal.ZERO) <= 0) {
            // No contribution history, can't estimate
            // Return deadline if set, otherwise null (handled by returning max date)
            return goal.getDeadline() != null ? goal.getDeadline() : null;
        }

        // Calculate months needed
        BigDecimal monthsNeeded = remaining.divide(monthlyContribution, 0, RoundingMode.CEILING);
        long months = monthsNeeded.longValue();

        // Limit to reasonable future (10 years max)
        if (months > 120) {
            return LocalDate.now().plusYears(10);
        }

        return LocalDate.now().plusMonths(months);
    }

    private BigDecimal calculateAverageMonthlyContribution(UUID userId, UUID linkedAccountId, LocalDate since) {
        // Get income transactions from the period
        try {
            List<TransactionEntity> transactions;
            if (linkedAccountId != null) {
                transactions = transactionRepository.findByAccountIdAndTransactionDateAfterOrderByTransactionDateDesc(
                        linkedAccountId, since);
            } else {
                transactions = transactionRepository.findByUserIdAndTransactionDateAfterOrderByTransactionDateDesc(
                        userId, since);
            }

            if (transactions.isEmpty()) {
                return BigDecimal.ZERO;
            }

            // Sum all positive transactions (contributions/savings)
            BigDecimal totalContributions = transactions.stream()
                    .map(TransactionEntity::getAmount)
                    .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate number of months
            long monthsBetween = ChronoUnit.MONTHS.between(since, LocalDate.now());
            if (monthsBetween <= 0) {
                monthsBetween = 1;
            }

            return totalContributions.divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            // If transaction repository doesn't have the required methods yet, return zero
            return BigDecimal.ZERO;
        }
    }

    // ==================== Helper Methods ====================

    private FinancialGoalEntity findGoalAndVerifyOwnership(UUID userId, UUID goalId) {
        FinancialGoalEntity entity = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return entity;
    }

    private FinancialGoalDto mapToDto(FinancialGoalEntity entity) {
        List<MilestoneDto> milestoneDtos = entity.getMilestones().stream()
                .map(this::mapMilestoneToDto)
                .collect(Collectors.toList());

        LocalDate estimatedCompletion = calculateEstimatedCompletionDate(entity);

        return new FinancialGoalDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTargetAmount(),
                entity.getCurrentAmount(),
                entity.getDeadline(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getIcon(),
                entity.getColor(),
                entity.getLinkedAccountId(),
                entity.getProgressPercent(),
                estimatedCompletion,
                milestoneDtos
        );
    }

    private MilestoneDto mapMilestoneToDto(MilestoneEntity entity) {
        return new MilestoneDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTargetAmount(),
                entity.getTargetDate(),
                entity.isAchieved(),
                entity.getAchievedAt(),
                entity.getDisplayOrder()
        );
    }
}
