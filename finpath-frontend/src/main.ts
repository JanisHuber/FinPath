import 'zone.js';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { environment } from './environments/environment';

async function start() {
  if (environment.useMocks) {
    const { worker } = await import('./mocks/browser');
    // WICHTIG: Root-Scope -> /mockServiceWorker.js muss vom Server erreichbar sein.
    await worker.start({ onUnhandledRequest: 'bypass' });
    console.log('[MSW] gestartet');
  }
  await bootstrapApplication(AppComponent, appConfig);
}

start().catch(err => console.error('❌ Bootstrap error:', err));
