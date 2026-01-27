package ch.finpath.persistence.learning;

import ch.finpath.persistence.enums.LearningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgressEntity, UUID> {
    List<LearningProgressEntity> findByUserId(UUID userId);

    Optional<LearningProgressEntity> findByUserIdAndModuleId(UUID userId, UUID moduleId);

    List<LearningProgressEntity> findByUserIdAndStatus(UUID userId, LearningStatus status);

    @Query("SELECT COUNT(p) FROM LearningProgressEntity p WHERE p.userId = :userId AND p.status = 'completed'")
    long countCompletedByUserId(@Param("userId") UUID userId);

    @Query("SELECT AVG(p.progressPercent) FROM LearningProgressEntity p WHERE p.userId = :userId")
    Double getAverageProgressByUserId(@Param("userId") UUID userId);
}
