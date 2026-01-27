package ch.finpath.persistence.accounts;

import ch.finpath.persistence.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUserIdOrderByDisplayOrder(UUID userId);
    List<AccountEntity> findByUserIdAndIsActiveOrderByDisplayOrder(UUID userId, boolean isActive);
    List<AccountEntity> findByUserIdAndAccountType(UUID userId, AccountType accountType);
}
