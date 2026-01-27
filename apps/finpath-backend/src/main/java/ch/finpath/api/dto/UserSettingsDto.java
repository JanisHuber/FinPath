package ch.finpath.api.dto;

import java.util.UUID;

public record UserSettingsDto(
    UUID id,
    String language,
    String theme,
    String currency,
    boolean notificationsInApp,
    boolean notificationsEmail,
    boolean notificationsPush,
    boolean privacyAnalytics,
    boolean privacyPersonalization,
    boolean onboardingCompleted
) {}
