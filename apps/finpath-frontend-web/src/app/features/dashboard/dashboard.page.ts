import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CheckInComponent } from './components/check-in/check-in.component';
import { FinanceSummaryComponent } from './components/finance-summary/finance-summary.component';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TranslateModule,
    CheckInComponent,
    FinanceSummaryComponent
  ],
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.css'],
})
export class DashboardPage {
  private authService = inject(AuthService);
  profile$ = this.authService.currentProfile$;
}
