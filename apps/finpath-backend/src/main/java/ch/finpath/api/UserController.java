package ch.finpath.api;

import ch.finpath.api.dto.CreateProfileRequest;
import ch.finpath.api.dto.ProfileDto;
import ch.finpath.persistence.profiles.ProfileEntity;
import ch.finpath.persistence.profiles.ProfilesRepository;
import ch.finpath.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private final ProfilesRepository profilesRepository;

    public UserController(ProfilesRepository profilesRepository) {
        this.profilesRepository = profilesRepository;
    }

    @GetMapping
    public ProfileDto me(@AuthenticationPrincipal AuthenticatedUser user) {
        ProfileEntity entity = profilesRepository.findById(user.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        System.out.println(entity);
        return new ProfileDto(entity.getDisplayName(), entity.getId());
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateProfileRequest request
    ) {
        if (profilesRepository.existsById(user.id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists");
        }

        ProfileEntity entity = new ProfileEntity(user.id(), request.displayName());
        ProfileEntity saved = profilesRepository.save(entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ProfileDto(saved.getDisplayName(), saved.getId()));
    }
}
