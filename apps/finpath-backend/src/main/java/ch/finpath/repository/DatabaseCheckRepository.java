package ch.finpath.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class DatabaseCheckRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Instant getCurrentDatabaseTime() {
        return (Instant) entityManager
                .createNativeQuery("SELECT now()")
                .getSingleResult();
    }
}
