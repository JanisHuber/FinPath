package ch.finpath.persistence.goals;

import ch.finpath.persistence.enums.GoalCategory;
import ch.finpath.persistence.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoalEntity, UUID> {
    List<FinancialGoalEntity> findByUserIdOrderByPriorityAsc(UUID userId);
    List<FinancialGoalEntity> findByUserIdAndStatusOrderByPriorityAsc(UUID userId, GoalStatus status);
    List<FinancialGoalEntity> findByUserIdAndCategoryOrderByPriorityAsc(UUID userId, GoalCategory category);
    List<FinancialGoalEntity> findByUserIdAndStatusAndCategoryOrderByPriorityAsc(UUID userId, GoalStatus status, GoalCategory category);
}
