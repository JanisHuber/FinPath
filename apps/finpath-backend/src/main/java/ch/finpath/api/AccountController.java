package ch.finpath.api;

import ch.finpath.api.dto.AccountDto;
import ch.finpath.api.dto.CreateAccountRequest;
import ch.finpath.api.dto.UpdateAccountRequest;
import ch.finpath.security.AuthenticatedUser;
import ch.finpath.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountDto> getAccounts(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return accountService.getActiveAccounts(user.id());
        }
        return accountService.getAccounts(user.id());
    }

    @GetMapping("/{id}")
    public AccountDto getAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return accountService.getAccount(user.id(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(user.id(), request);
    }

    @PutMapping("/{id}")
    public AccountDto updateAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request) {
        return accountService.updateAccount(user.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        accountService.deleteAccount(user.id(), id);
    }

    @PostMapping("/init-defaults")
    public ResponseEntity<Void> initDefaultAccounts(@AuthenticationPrincipal AuthenticatedUser user) {
        accountService.createDefaultAccounts(user.id());
        return ResponseEntity.ok().build();
    }
}
