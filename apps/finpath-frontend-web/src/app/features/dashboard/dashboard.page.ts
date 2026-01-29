import {Component, inject, OnInit} from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard-page',
  imports: [AsyncPipe],
  templateUrl: './dashboard.page.html',
  standalone: true,
  styleUrls: ['./dashboard.page.css'],
})
export class DashboardPage implements OnInit {
  private authService = inject(AuthService);
  profile$ = this.authService.currentProfile$;

  ngOnInit(): void {
    console.log(this.profile$);
  }
}
