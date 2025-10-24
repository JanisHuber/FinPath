import { HttpInterceptorFn } from '@angular/common/http';

export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    // hier globales Error-Handling (z.B. toast), RxJS catchError(...)
  );
