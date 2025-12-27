package ch.finpath.controller;

import ch.finpath.repository.DatabaseCheckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatabaseController.class)
class DatabaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatabaseCheckRepository repository;

    @Test
    void testDatabase_ShouldReturnSuccess() throws Exception {
        LocalDateTime testTime = LocalDateTime.now();
        when(repository.getCurrentDatabaseTime()).thenReturn(testTime);

        mockMvc.perform(get("/api/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("connected"))
                .andExpect(jsonPath("$.dbTime").exists());
    }

    @Test
    void testDatabase_ShouldHandleError() throws Exception {
        when(repository.getCurrentDatabaseTime())
                .thenThrow(new RuntimeException("Connection failed"));

        mockMvc.perform(get("/api/db"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").exists());
    }
}
