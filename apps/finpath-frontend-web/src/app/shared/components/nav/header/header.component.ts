import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '@core/services/auth.service';
import { Profile } from '@models/profile.models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private translate = inject(TranslateService);
  private subscriptions: Subscription[] = [];

  currentLang = 'de';
  profile: Profile | null = null;
  unreadNotificationsCount = 0;
  showNotificationsDropdown = false;
  showUserDropdown = false;

  ngOnInit(): void {
    this.currentLang = this.translate.currentLang || 'de';

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

  get userEmail(): string {
    const user = this.authService.getCurrentUser();
    return user?.email || '';
  }

  switchLang(lang: string): void {
    this.currentLang = lang;
    this.translate.use(lang);
  }

  toggleNotifications(): void {
    this.showNotificationsDropdown = !this.showNotificationsDropdown;
    this.showUserDropdown = false;
  }

  toggleUserDropdown(): void {
    this.showUserDropdown = !this.showUserDropdown;
    this.showNotificationsDropdown = false;
  }

  async logout(): Promise<void> {
    await this.authService.logout();
  }
}
