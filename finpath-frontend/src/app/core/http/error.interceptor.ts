import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        router.navigate(['/login']);
      } else if (error.status === 403) {
        router.navigate(['/forbidden']);
      } else if (error.status === 500) {
        console.error('Serverfehler:', error.message);
      }

      // Optional: Nutzerfreundliche Meldung
      // toastService.show('Etwas ist schiefgelaufen');

      return throwError(() => error);
    })
  );
};