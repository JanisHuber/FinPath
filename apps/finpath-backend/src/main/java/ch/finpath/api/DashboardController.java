package ch.finpath.api;

import ch.finpath.api.dto.CheckInDto;
import ch.finpath.api.dto.FinancialSummaryDto;
import ch.finpath.security.AuthenticatedUser;
import ch.finpath.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/financial-summary")
    public FinancialSummaryDto getFinancialSummary(@AuthenticationPrincipal AuthenticatedUser user) {
        return dashboardService.getFinancialSummary(user.id());
    }

    @GetMapping("/check-in")
    public CheckInDto getCheckIn(@AuthenticationPrincipal AuthenticatedUser user) {
        return dashboardService.getCheckIn(user.id());
    }
}
