package ch.finpath.api.dto;

import java.math.BigDecimal;

public record FinancialSummaryDto(
    BigDecimal monthlyIncome,
    BigDecimal monthlyExpenses,
    BigDecimal liquidity,
    BigDecimal liquidityRate,
    BigDecimal netWorth,
    BigDecimal netWorthRate
) {}
