package ch.finpath.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DatabaseCheckRepository extends JpaRepository<Object, Long> {

    @Query(value = "SELECT now()", nativeQuery = true)
    LocalDateTime getCurrentDatabaseTime();
}
