package ch.finpath.api.dto;

public record RecommendationDto(
    String text,
    String type,
    String priority,
    String link
) {}
