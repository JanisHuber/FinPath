package ch.finpath.service;

import ch.finpath.api.dto.*;
import ch.finpath.persistence.accounts.AccountEntity;
import ch.finpath.persistence.accounts.AccountRepository;
import ch.finpath.persistence.enums.TransactionType;
import ch.finpath.persistence.transactions.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public FinancialSummaryDto getFinancialSummary(UUID userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfMonth.minusDays(1);

        // Get monthly income and expenses
        BigDecimal monthlyIncome = getTransactionSum(userId, TransactionType.income, startOfMonth, endOfMonth);
        BigDecimal monthlyExpenses = getTransactionSum(userId, TransactionType.expense, startOfMonth, endOfMonth);

        // Calculate liquidity (total balance of all accounts)
        BigDecimal liquidity = calculateTotalBalance(userId);

        // Calculate last month's liquidity for rate comparison
        BigDecimal lastMonthIncome = getTransactionSum(userId, TransactionType.income, startOfLastMonth, endOfLastMonth);
        BigDecimal lastMonthExpenses = getTransactionSum(userId, TransactionType.expense, startOfLastMonth, endOfLastMonth);
        BigDecimal lastMonthNet = lastMonthIncome.subtract(lastMonthExpenses);
        BigDecimal lastMonthLiquidity = liquidity.subtract(monthlyIncome.subtract(monthlyExpenses)).add(lastMonthNet);

        // Calculate liquidity rate (percentage change)
        BigDecimal liquidityRate = calculatePercentageChange(lastMonthLiquidity, liquidity);

        // Net worth (same as liquidity for now, could include investments later)
        BigDecimal netWorth = liquidity;
        BigDecimal netWorthRate = liquidityRate;

        return new FinancialSummaryDto(
                monthlyIncome,
                monthlyExpenses,
                liquidity,
                liquidityRate,
                netWorth,
                netWorthRate
        );
    }

    public CheckInDto getCheckIn(UUID userId) {
        FinancialSummaryDto summary = getFinancialSummary(userId);

        // Calculate financial score (0-100)
        int score = calculateFinancialScore(summary);

        // Determine status based on score
        String status;
        if (score >= 70) {
            status = "Good";
        } else if (score >= 40) {
            status = "Average";
        } else {
            status = "Bad";
        }

        // Generate recommendations
        List<RecommendationDto> recommendations = generateRecommendations(summary, score);

        // Context tip based on score
        String contextTip = generateContextTip(score);

        FinancialScoreDto financialScore = new FinancialScoreDto(score, contextTip);

        return new CheckInDto(status, recommendations, financialScore);
    }

    private BigDecimal getTransactionSum(UUID userId, TransactionType type, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = transactionRepository.sumAmountByUserIdAndTypeAndDateRange(userId, type, startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalBalance(UUID userId) {
        List<AccountEntity> accounts = accountRepository.findByUserIdAndIsActiveOrderByDisplayOrder(userId, true);
        return accounts.stream()
                .map(AccountEntity::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return newValue.subtract(oldValue)
                .divide(oldValue.abs(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private int calculateFinancialScore(FinancialSummaryDto summary) {
        int score = 50; // Base score

        // Positive income vs expenses ratio
        if (summary.monthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = summary.monthlyExpenses().divide(summary.monthlyIncome(), 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(BigDecimal.valueOf(0.5)) < 0) {
                score += 25; // Expenses less than 50% of income
            } else if (ratio.compareTo(BigDecimal.valueOf(0.8)) < 0) {
                score += 15; // Expenses less than 80% of income
            } else if (ratio.compareTo(BigDecimal.ONE) < 0) {
                score += 5; // Expenses less than income
            } else {
                score -= 15; // Expenses exceed income
            }
        }

        // Positive liquidity growth
        if (summary.liquidityRate().compareTo(BigDecimal.ZERO) > 0) {
            score += 15;
        } else if (summary.liquidityRate().compareTo(BigDecimal.valueOf(-10)) > 0) {
            score += 5;
        } else {
            score -= 10;
        }

        // Positive liquidity amount
        if (summary.liquidity().compareTo(BigDecimal.ZERO) > 0) {
            score += 10;
        } else {
            score -= 20;
        }

        return Math.max(0, Math.min(100, score));
    }

    private List<RecommendationDto> generateRecommendations(FinancialSummaryDto summary, int score) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        // Check if expenses exceed income
        if (summary.monthlyExpenses().compareTo(summary.monthlyIncome()) > 0) {
            recommendations.add(new RecommendationDto(
                    "Deine Ausgaben übersteigen deine Einnahmen. Überprüfe deine Ausgaben.",
                    "Finance",
                    "High",
                    "/finance-path"
            ));
        }

        // Check for negative liquidity trend
        if (summary.liquidityRate().compareTo(BigDecimal.valueOf(-5)) < 0) {
            recommendations.add(new RecommendationDto(
                    "Deine Liquidität ist rückläufig. Setze dir ein Sparziel.",
                    "Finance",
                    "Medium",
                    "/finance-path"
            ));
        }

        // Suggest learning if score is low
        if (score < 50) {
            recommendations.add(new RecommendationDto(
                    "Verbessere dein Finanzwissen mit unseren Lernmodulen.",
                    "Learning",
                    "Medium",
                    "/learning"
            ));
        }

        // If no issues, give positive feedback
        if (recommendations.isEmpty()) {
            recommendations.add(new RecommendationDto(
                    "Du machst das großartig! Halte deine Ausgaben weiterhin im Blick.",
                    "Finance",
                    "Low",
                    null
            ));
        }

        return recommendations;
    }

    private String generateContextTip(int score) {
        if (score >= 80) {
            return "Exzellent! Du hast deine Finanzen bestens im Griff.";
        } else if (score >= 60) {
            return "Gut! Es gibt noch etwas Optimierungspotenzial.";
        } else if (score >= 40) {
            return "Durchschnittlich. Schau dir die Empfehlungen an.";
        } else {
            return "Achtung! Deine Finanzen brauchen Aufmerksamkeit.";
        }
    }
}
