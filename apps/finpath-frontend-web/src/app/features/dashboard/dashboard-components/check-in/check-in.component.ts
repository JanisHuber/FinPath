import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckInService } from '@app/core/services/check-in.service';
import { CheckIn, Recommendation } from '@app/core/models/check-in.models';

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

  constructor(private checkInService: CheckInService) {}

  ngOnInit(): void {
    this.checkInService.getCheckIn().subscribe(
      (data) => {
        console.log('Check-In data received:', data);
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
      (error) => {
        console.error('Error fetching Check-In data:', error);
      }
    );
  }
}
