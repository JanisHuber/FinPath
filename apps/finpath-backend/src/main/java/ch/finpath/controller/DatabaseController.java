package ch.finpath.controller;

import ch.finpath.repository.DatabaseCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    private static final Logger log = LoggerFactory.getLogger(DatabaseController.class);
    private final DatabaseCheckRepository repository;

    public DatabaseController(DatabaseCheckRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> testDatabase() {
        Map<String, String> result = new HashMap<>();

        try {
            LocalDateTime dbTime = repository.getCurrentDatabaseTime();
            result.put("dbTime", dbTime.toString());
            result.put("status", "connected");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Log full error details server-side for debugging
            log.error("Database connection test failed", e);

            // Return generic error message to client (don't expose internal details)
            result.put("error", "Database connection failed");
            result.put("status", "error");
            return ResponseEntity.status(500).body(result);
        }
    }
}
