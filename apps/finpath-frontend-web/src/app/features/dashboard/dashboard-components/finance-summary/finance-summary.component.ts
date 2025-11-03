import { Component, OnInit } from '@angular/core';
import { FinancialSummary } from '@app/core/models/financial-summary.models';
import { FinancialSummaryService } from '@app/core/services/financial-summary.service';

@Component({
  selector: 'app-finance-summary',
  standalone: true,
  imports: [],
  templateUrl: './finance-summary.component.html',
  styleUrls: ['./finance-summary.component.css']
})
export class FinanceSummaryComponent implements OnInit {

  financialSummaryData: FinancialSummary | null = null;

  constructor(private financialSummaryService: FinancialSummaryService) { }

  ngOnInit() {
    this.financialSummaryService.getFinancialSummary().subscribe({
      next: (data) => {
        this.financialSummaryData = data;
      },
      error: (error) => {
        console.error('Error fetching Financial Summary data:', error);
      }
    });
  }

}
