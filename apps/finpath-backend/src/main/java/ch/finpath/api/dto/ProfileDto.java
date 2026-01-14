package ch.finpath.api.dto;

import java.util.UUID;

public record ProfileDto(
        String displayName,
        UUID id
) {}