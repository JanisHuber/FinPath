package ch.finpath.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class DatabaseCheckRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public LocalDateTime getCurrentDatabaseTime() {
        return (LocalDateTime) entityManager
                .createNativeQuery("SELECT now()")
                .getSingleResult();
    }
}
