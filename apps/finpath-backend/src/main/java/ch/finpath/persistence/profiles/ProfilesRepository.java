package ch.finpath.persistence.profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfilesRepository extends JpaRepository<ProfileEntity, UUID> {
}
