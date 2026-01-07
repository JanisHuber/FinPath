import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FinancialSummary } from '@models/financial-summary.models';
import { FinancialSummaryService } from '@core/services/financial-summary.service';

@Component({
  selector: 'app-finance-summary',
  standalone: true,
  imports: [],
  templateUrl: './finance-summary.component.html',
  styleUrls: ['./finance-summary.component.css']
})
export class FinanceSummaryComponent implements OnInit {
  financialSummaryData: FinancialSummary | null = null;

  private destroyRef = inject(DestroyRef);

  constructor(private financialSummaryService: FinancialSummaryService) {}

  ngOnInit(): void {
    this.financialSummaryService.getFinancialSummary()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.financialSummaryData = data;
        },
        error: (error) => {
          console.error('Error fetching Financial Summary data:', error);
        }
      });
  }
}
