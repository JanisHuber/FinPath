package ch.finpath.persistence.goals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<MilestoneEntity, UUID> {
    List<MilestoneEntity> findByGoalIdOrderByDisplayOrder(UUID goalId);
    List<MilestoneEntity> findByGoalIdAndIsAchievedOrderByDisplayOrder(UUID goalId, boolean isAchieved);
}
