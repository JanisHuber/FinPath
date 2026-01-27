package ch.finpath.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateSettingsRequest(
    @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
    @Pattern(regexp = "^(de|fr|en)$", message = "Language must be de, fr, or en")
    String language,

    @Pattern(regexp = "^(light|dark|system)$", message = "Theme must be light, dark, or system")
    String theme,

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    String currency,

    Boolean notificationsInApp,
    Boolean notificationsEmail,
    Boolean notificationsPush,
    Boolean privacyAnalytics,
    Boolean privacyPersonalization
) {}
