import { Component, inject } from '@angular/core';
import { CheckInComponent } from './components/check-in/check-in.component';
import { FinanceSummaryComponent } from './components/finance-summary/finance-summary.component';
import { AuthService } from '@core/services/auth.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard-page',
  imports: [CheckInComponent, FinanceSummaryComponent, AsyncPipe],
  templateUrl: './dashboard.page.html',
  standalone: true,
  styleUrls: ['./dashboard.page.css'],
})
export class DashboardPage {
  private authService = inject(AuthService);
  profile$ = this.authService.currentProfile$;

  ngOnInit(): void {
    console.log(this.profile$);
  }
}
