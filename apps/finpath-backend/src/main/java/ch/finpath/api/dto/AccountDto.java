package ch.finpath.api.dto;

import ch.finpath.persistence.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDto(
    UUID id,
    String name,
    String description,
    AccountType accountType,
    BigDecimal balance,
    String currency,
    String icon,
    String color,
    boolean isDefault,
    boolean isActive,
    int displayOrder
) {}
