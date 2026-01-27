package ch.finpath.service;

import ch.finpath.api.dto.ProfileDto;
import ch.finpath.api.dto.UpdateProfileRequest;
import ch.finpath.persistence.profiles.ProfileEntity;
import ch.finpath.persistence.profiles.ProfilesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfilesRepository profilesRepository;

    public ProfileService(ProfilesRepository profilesRepository) {
        this.profilesRepository = profilesRepository;
    }

    public ProfileDto getProfile(UUID userId) {
        ProfileEntity entity = profilesRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return mapToDto(entity);
    }

    @Transactional
    public ProfileDto updateProfile(UUID userId, UpdateProfileRequest request) {
        ProfileEntity entity = profilesRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        if (request.displayName() != null) {
            entity.setDisplayName(request.displayName());
        }

        ProfileEntity saved = profilesRepository.save(entity);
        return mapToDto(saved);
    }

    private ProfileDto mapToDto(ProfileEntity entity) {
        return new ProfileDto(entity.getDisplayName(), entity.getId());
    }
}
