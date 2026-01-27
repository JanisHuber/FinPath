package ch.finpath.api;

import ch.finpath.api.dto.ProfileDto;
import ch.finpath.api.dto.UpdateProfileRequest;
import ch.finpath.api.dto.UpdateSettingsRequest;
import ch.finpath.api.dto.UserSettingsDto;
import ch.finpath.security.AuthenticatedUser;
import ch.finpath.service.ProfileService;
import ch.finpath.service.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final ProfileService profileService;
    private final UserSettingsService userSettingsService;

    public UserController(ProfileService profileService, UserSettingsService userSettingsService) {
        this.profileService = profileService;
        this.userSettingsService = userSettingsService;
    }

    @GetMapping("/me")
    public ProfileDto getProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return profileService.getProfile(user.id());
    }

    @PutMapping("/me")
    public ProfileDto updateProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(user.id(), request);
    }

    @GetMapping("/settings")
    public UserSettingsDto getSettings(@AuthenticationPrincipal AuthenticatedUser user) {
        return userSettingsService.getSettings(user.id());
    }

    @PutMapping("/settings")
    public UserSettingsDto updateSettings(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateSettingsRequest request) {
        return userSettingsService.updateSettings(user.id(), request);
    }

    @PostMapping("/settings/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding(@AuthenticationPrincipal AuthenticatedUser user) {
        userSettingsService.markOnboardingCompleted(user.id());
        return ResponseEntity.ok().build();
    }
}
