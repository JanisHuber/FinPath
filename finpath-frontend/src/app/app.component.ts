import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="min-h-dvh bg-gray-50 text-gray-900">
      <header class="border-b bg-white">
        <div class="mx-auto max-w-7xl p-4 font-semibold">FinPath</div>
      </header>
      <main class="mx-auto max-w-7xl p-4">
        <router-outlet />
      </main>
    </div>
  `,
})
export class AppComponent {}
