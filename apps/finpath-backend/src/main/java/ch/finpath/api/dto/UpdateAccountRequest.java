package ch.finpath.api.dto;

import ch.finpath.persistence.enums.AccountType;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateAccountRequest(
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    String name,

    @Size(max = 500, message = "Description must be at most 500 characters")
    String description,

    AccountType accountType,

    BigDecimal balance,

    @Size(max = 3, message = "Currency must be 3 characters")
    String currency,

    @Size(max = 50, message = "Icon must be at most 50 characters")
    String icon,

    @Size(max = 7, message = "Color must be at most 7 characters")
    String color,

    Boolean isDefault,
    Boolean isActive,
    Integer displayOrder
) {}
