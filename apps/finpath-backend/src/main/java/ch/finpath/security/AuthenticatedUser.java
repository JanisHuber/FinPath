package ch.finpath.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String email,
        String displayName
) {}

