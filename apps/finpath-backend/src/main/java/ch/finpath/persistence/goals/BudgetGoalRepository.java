package ch.finpath.persistence.goals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetGoalRepository extends JpaRepository<BudgetGoalEntity, UUID> {
    List<BudgetGoalEntity> findByUserIdAndIsActiveOrderByCategory(UUID userId, boolean isActive);
    List<BudgetGoalEntity> findByUserIdOrderByCategory(UUID userId);
    List<BudgetGoalEntity> findByUserIdAndCategory(UUID userId, String category);
}
