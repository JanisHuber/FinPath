import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/components/nav/header/header.component';
import { SidebarComponent } from './shared/components/nav/side-bar/side-bar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header, SidebarComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  sidebarOpen = true;              // Desktop default: offen
  mobileDrawerOpen = false;        // separates Overlay auf Mobile

  toggleSidebar() {
    // Desktop: Toggle Breite, Mobile: Drawer Overlay
    if (window.matchMedia('(max-width: 1023px)').matches) {
      this.mobileDrawerOpen = !this.mobileDrawerOpen;
    } else {
      this.sidebarOpen = !this.sidebarOpen;
    }
  }

  closeMobileDrawer() {
    this.mobileDrawerOpen = false;
  }
}
