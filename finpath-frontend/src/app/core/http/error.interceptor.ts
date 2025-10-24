import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((error) => {
      // hier globales Error-Handling (z.B. toast)
      console.error('HTTP error intercepted:', error);
      return throwError(() => error);
    })
  );
