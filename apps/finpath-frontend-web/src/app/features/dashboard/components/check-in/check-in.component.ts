import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckInService } from '@core/services/check-in.service';
import { CheckIn, Recommendation } from '@models/check-in.models';

@Component({
  selector: 'app-check-in',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './check-in.component.html',
  styleUrls: ['./check-in.component.css']
})
export class CheckInComponent implements OnInit {
  checkInData: CheckIn | null = null;
  bestRecommendation: Recommendation | null = null;

  private destroyRef = inject(DestroyRef);

  constructor(private checkInService: CheckInService) {}

  ngOnInit(): void {
    this.checkInService.getCheckIn()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.checkInData = data;

          if (data?.recommendations && data.recommendations.length > 0) {
            const priorityOrder: Record<string, number> = { High: 3, Medium: 2, Low: 1 };
            this.bestRecommendation = data.recommendations.reduce((best, current) =>
              (priorityOrder[current.priority] ?? 0) > (priorityOrder[best.priority] ?? 0) ? current : best,
              data.recommendations[0]
            );
          } else {
            this.bestRecommendation = null;
          }
        },
        error: (error) => {
          console.error('Error fetching Check-In data:', error);
        }
      });
  }
}
