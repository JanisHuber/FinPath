package ch.finpath.controller;

import ch.finpath.api.UserController;
import ch.finpath.persistence.profiles.ProfileEntity;
import ch.finpath.persistence.profiles.ProfilesRepository;
import ch.finpath.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfilesRepository profilesRepository;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String TEST_EMAIL = "test@example.com";

    private UsernamePasswordAuthenticationToken createAuth() {
        AuthenticatedUser user = new AuthenticatedUser(TEST_USER_ID, TEST_EMAIL);
        return new UsernamePasswordAuthenticationToken(user, null, List.of());
    }

    @Test
    void getProfile_ShouldReturnProfile() throws Exception {
        ProfileEntity entity = new ProfileEntity(TEST_USER_ID, "Test User");
        when(profilesRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/api/me")
                        .with(authentication(createAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.displayName").value("Test User"));
    }

    @Test
    void getProfile_ShouldReturn404WhenNotFound() throws Exception {
        when(profilesRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/me")
                        .with(authentication(createAuth())))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProfile_ShouldCreateAndReturnProfile() throws Exception {
        when(profilesRepository.existsById(TEST_USER_ID)).thenReturn(false);

        ProfileEntity savedEntity = new ProfileEntity(TEST_USER_ID, "New User");
        when(profilesRepository.save(any(ProfileEntity.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/me")
                        .with(authentication(createAuth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\": \"New User\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.displayName").value("New User"));
    }

    @Test
    void createProfile_ShouldReturn409WhenProfileExists() throws Exception {
        when(profilesRepository.existsById(TEST_USER_ID)).thenReturn(true);

        mockMvc.perform(post("/api/me")
                        .with(authentication(createAuth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\": \"New User\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getProfile_ShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }
}
