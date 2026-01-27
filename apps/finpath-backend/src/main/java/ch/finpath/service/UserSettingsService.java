package ch.finpath.service;

import ch.finpath.api.dto.UpdateSettingsRequest;
import ch.finpath.api.dto.UserSettingsDto;
import ch.finpath.persistence.settings.UserSettingsEntity;
import ch.finpath.persistence.settings.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettingsDto getSettings(UUID userId) {
        UserSettingsEntity entity = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
        return mapToDto(entity);
    }

    @Transactional
    public UserSettingsDto updateSettings(UUID userId, UpdateSettingsRequest request) {
        UserSettingsEntity entity = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));

        if (request.language() != null) {
            entity.setLanguage(request.language());
        }
        if (request.theme() != null) {
            entity.setTheme(request.theme());
        }
        if (request.currency() != null) {
            entity.setCurrency(request.currency());
        }
        if (request.notificationsInApp() != null) {
            entity.setNotificationsInApp(request.notificationsInApp());
        }
        if (request.notificationsEmail() != null) {
            entity.setNotificationsEmail(request.notificationsEmail());
        }
        if (request.notificationsPush() != null) {
            entity.setNotificationsPush(request.notificationsPush());
        }
        if (request.privacyAnalytics() != null) {
            entity.setPrivacyAnalytics(request.privacyAnalytics());
        }
        if (request.privacyPersonalization() != null) {
            entity.setPrivacyPersonalization(request.privacyPersonalization());
        }

        UserSettingsEntity saved = userSettingsRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public void markOnboardingCompleted(UUID userId) {
        UserSettingsEntity entity = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
        entity.setOnboardingCompleted(true);
        userSettingsRepository.save(entity);
    }

    private UserSettingsEntity createDefaultSettings(UUID userId) {
        UserSettingsEntity entity = new UserSettingsEntity(userId);
        return userSettingsRepository.save(entity);
    }

    private UserSettingsDto mapToDto(UserSettingsEntity entity) {
        return new UserSettingsDto(
                entity.getId(),
                entity.getLanguage(),
                entity.getTheme(),
                entity.getCurrency(),
                entity.isNotificationsInApp(),
                entity.isNotificationsEmail(),
                entity.isNotificationsPush(),
                entity.isPrivacyAnalytics(),
                entity.isPrivacyPersonalization(),
                entity.isOnboardingCompleted()
        );
    }
}
