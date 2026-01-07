import { Component } from '@angular/core';
import { CheckInComponent } from '../dashboard-components/check-in/check-in.component';
import { FinanceSummaryComponent } from '../dashboard-components/finance-summary/finance-summary.component';

@Component({
  selector: 'app-dashboard-page',
  imports: [CheckInComponent, FinanceSummaryComponent],
  templateUrl: './dashboard.page.html',
  standalone: true,
  styleUrls: ['./dashboard.page.css']
})

export class DashboardPage {

}