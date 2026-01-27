package ch.finpath.api.dto;

import ch.finpath.persistence.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @Size(max = 500, message = "Description must be at most 500 characters")
    String description,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    BigDecimal initialBalance,

    @Size(max = 3, message = "Currency must be 3 characters")
    String currency,

    @Size(max = 50, message = "Icon must be at most 50 characters")
    String icon,

    @Size(max = 7, message = "Color must be at most 7 characters")
    String color
) {}
