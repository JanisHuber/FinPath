package ch.finpath.persistence.family;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMemberEntity, UUID> {
    List<FamilyMemberEntity> findByOwnerUserIdAndIsActiveOrderByName(UUID ownerUserId, boolean isActive);
    List<FamilyMemberEntity> findByOwnerUserIdOrderByName(UUID ownerUserId);
}
