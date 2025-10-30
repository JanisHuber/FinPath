import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { APP_ROUTES } from '@core/routing/app.routes';
import { authInterceptor } from '@core/http/auth.interceptor';
import { errorInterceptor } from '@core/http/error.interceptor';
import { PreloadAllModules } from '@angular/router';
import { withPreloading } from '@angular/router';
import { withInMemoryScrolling } from '@angular/router';


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES, withInMemoryScrolling({ scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled' }),
      withPreloading(PreloadAllModules)),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
  ],
};
