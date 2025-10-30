import 'zone.js';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { environment } from './environments/environment';


(async function start() {
  try {
    if (environment.useMocks) {
      const { worker } = await import('./mocks/browser');
      await worker.start({ onUnhandledRequest: 'bypass' });
      console.log('[MSW] running');
    }
    await bootstrapApplication(AppComponent, appConfig);
  } catch (err) {
    console.error('❌ Bootstrap error:', err);
  }
})();
