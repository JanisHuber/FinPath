package ch.finpath.controller;

import ch.finpath.repository.DatabaseCheckRepository;
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
            result.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            result.put("status", "error");
            return ResponseEntity.status(500).body(result);
        }
    }
}
