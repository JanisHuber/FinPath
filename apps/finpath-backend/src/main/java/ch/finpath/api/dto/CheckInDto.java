package ch.finpath.api.dto;

import java.util.List;

public record CheckInDto(
    String status,
    List<RecommendationDto> recommendations,
    FinancialScoreDto financialScore
) {}
