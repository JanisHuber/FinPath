import { Component } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { HeaderComponent } from '@shared/components/nav/header/header.component';
import { SidebarComponent } from '@shared/components/nav/side-bar/side-bar.component';
import { TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, SidebarComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  showNav = true;

  constructor(private translate: TranslateService, private router: Router) {
    translate.addLangs(['de', 'fr', 'en']);
    translate.setDefaultLang('de');
    translate.setFallbackLang('de');
    translate.use('de');

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        const authRoutes = ['/login', '/register'];
        this.showNav = !authRoutes.includes(event.urlAfterRedirects);
      });
  }
}
