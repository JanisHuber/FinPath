import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '@shared/components/nav/header/header.component';
import { SidebarComponent } from '@shared/components/nav/side-bar/side-bar.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, SidebarComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  constructor(private translate: TranslateService) {
    translate.addLangs(['de', 'fr', 'en']);
    translate.setDefaultLang('de');
    translate.setFallbackLang('de');
    translate.use('de');
  }
}
