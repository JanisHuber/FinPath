package ch.finpath.service;

import ch.finpath.api.dto.AccountDto;
import ch.finpath.api.dto.CreateAccountRequest;
import ch.finpath.api.dto.UpdateAccountRequest;
import ch.finpath.persistence.accounts.AccountEntity;
import ch.finpath.persistence.accounts.AccountRepository;
import ch.finpath.persistence.enums.AccountType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDto> getAccounts(UUID userId) {
        return accountRepository.findByUserIdOrderByDisplayOrder(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AccountDto> getActiveAccounts(UUID userId) {
        return accountRepository.findByUserIdAndIsActiveOrderByDisplayOrder(userId, true)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccount(UUID userId, UUID accountId) {
        AccountEntity entity = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return mapToDto(entity);
    }

    @Transactional
    public AccountDto createAccount(UUID userId, CreateAccountRequest request) {
        AccountEntity entity = new AccountEntity(userId, request.name(), request.accountType());

        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.initialBalance() != null) {
            entity.setBalance(request.initialBalance());
        }
        if (request.currency() != null) {
            entity.setCurrency(request.currency());
        }
        if (request.icon() != null) {
            entity.setIcon(request.icon());
        }
        if (request.color() != null) {
            entity.setColor(request.color());
        }

        // Set display order to be after existing accounts
        List<AccountEntity> existingAccounts = accountRepository.findByUserIdOrderByDisplayOrder(userId);
        entity.setDisplayOrder(existingAccounts.size());

        AccountEntity saved = accountRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public AccountDto updateAccount(UUID userId, UUID accountId, UpdateAccountRequest request) {
        AccountEntity entity = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.accountType() != null) {
            entity.setAccountType(request.accountType());
        }
        if (request.balance() != null) {
            entity.setBalance(request.balance());
        }
        if (request.currency() != null) {
            entity.setCurrency(request.currency());
        }
        if (request.icon() != null) {
            entity.setIcon(request.icon());
        }
        if (request.color() != null) {
            entity.setColor(request.color());
        }
        if (request.isDefault() != null) {
            entity.setDefault(request.isDefault());
        }
        if (request.isActive() != null) {
            entity.setActive(request.isActive());
        }
        if (request.displayOrder() != null) {
            entity.setDisplayOrder(request.displayOrder());
        }

        AccountEntity saved = accountRepository.save(entity);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteAccount(UUID userId, UUID accountId) {
        AccountEntity entity = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        accountRepository.delete(entity);
    }

    @Transactional
    public void createDefaultAccounts(UUID userId) {
        // Check if user already has accounts
        List<AccountEntity> existingAccounts = accountRepository.findByUserIdOrderByDisplayOrder(userId);
        if (!existingAccounts.isEmpty()) {
            return;
        }

        // Create default accounts
        AccountEntity investAccount = new AccountEntity(userId, "Investieren", AccountType.invest);
        investAccount.setIcon("trending-up");
        investAccount.setColor("#10B981");
        investAccount.setDisplayOrder(0);
        investAccount.setDefault(true);

        AccountEntity spendingAccount = new AccountEntity(userId, "Ausgaben", AccountType.spending);
        spendingAccount.setIcon("credit-card");
        spendingAccount.setColor("#F59E0B");
        spendingAccount.setDisplayOrder(1);

        AccountEntity saveAccount = new AccountEntity(userId, "Sparen", AccountType.save);
        saveAccount.setIcon("piggy-bank");
        saveAccount.setColor("#3B82F6");
        saveAccount.setDisplayOrder(2);

        accountRepository.save(investAccount);
        accountRepository.save(spendingAccount);
        accountRepository.save(saveAccount);
    }

    private AccountDto mapToDto(AccountEntity entity) {
        return new AccountDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getAccountType(),
                entity.getBalance(),
                entity.getCurrency(),
                entity.getIcon(),
                entity.getColor(),
                entity.isDefault(),
                entity.isActive(),
                entity.getDisplayOrder()
        );
    }
}
