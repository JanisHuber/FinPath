package ch.finpath.persistence.learning;

import ch.finpath.persistence.enums.LearningCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearningModuleRepository extends JpaRepository<LearningModuleEntity, UUID> {
    List<LearningModuleEntity> findByIsPublishedOrderByDisplayOrder(boolean isPublished);
    List<LearningModuleEntity> findByCategoryAndIsPublishedOrderByDisplayOrder(LearningCategory category, boolean isPublished);
    List<LearningModuleEntity> findByDifficultyLevelAndIsPublishedOrderByDisplayOrder(int difficultyLevel, boolean isPublished);
}
