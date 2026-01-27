package ch.finpath.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters")
    String displayName
) {}
