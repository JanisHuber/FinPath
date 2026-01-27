package ch.finpath.persistence.transactions;

import ch.finpath.persistence.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByUserIdOrderByTransactionDateDesc(UUID userId);

    List<TransactionEntity> findByUserIdAndAccountIdOrderByTransactionDateDesc(UUID userId, UUID accountId);

    List<TransactionEntity> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId, LocalDate startDate, LocalDate endDate);

    List<TransactionEntity> findByUserIdAndTransactionTypeAndTransactionDateBetween(
            UUID userId, TransactionType transactionType, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.userId = :userId AND t.transactionType = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<TransactionEntity> findByUserIdAndCategoryAndTransactionDateBetween(
            UUID userId, String category, LocalDate startDate, LocalDate endDate);

    List<TransactionEntity> findByAccountIdAndTransactionDateAfterOrderByTransactionDateDesc(
            UUID accountId, LocalDate date);

    List<TransactionEntity> findByUserIdAndTransactionDateAfterOrderByTransactionDateDesc(
            UUID userId, LocalDate date);
}
