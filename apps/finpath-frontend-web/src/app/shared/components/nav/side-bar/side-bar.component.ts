import { Component, Output, EventEmitter, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { IconComponent } from '../icon-component/icon.component';
import { AuthService } from '@core/services/auth.service';
import { Profile } from '@models/profile.models';
import { Subscription } from 'rxjs';

interface NavItem {
  route: string;
  labelKey: string;
  icon: string;
}

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, TranslateModule, IconComponent],
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
})
export class SidebarComponent implements OnInit, OnDestroy {
  @Output() linkClick = new EventEmitter<void>();

  private authService = inject(AuthService);
  private subscriptions: Subscription[] = [];

  profile: Profile | null = null;

  navItems: NavItem[] = [
    { route: '/dashboard', labelKey: 'app.dashboard', icon: 'dashboard' },
    { route: '/finance-path', labelKey: 'app.finance-path', icon: 'finance-path' },
    { route: '/learning', labelKey: 'app.learning', icon: 'learning' },
    { route: '/settings/profile', labelKey: 'app.profile', icon: 'profile' },
    { route: '/settings', labelKey: 'app.settings', icon: 'settings' },
  ];

  ngOnInit(): void {
    this.subscriptions.push(
      this.authService.currentProfile$.subscribe(profile => {
        this.profile = profile;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  get userInitials(): string {
    if (!this.profile?.displayName) return '??';
    const parts = this.profile.displayName.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return this.profile.displayName.substring(0, 2).toUpperCase();
  }

  onLinkClick(): void {
    this.linkClick.emit();
  }
}
