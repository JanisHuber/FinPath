import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ApiError, isApiError } from '@models/api-error.models';

export interface EnhancedHttpError extends HttpErrorResponse {
  apiError?: ApiError;
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const enhancedError: EnhancedHttpError = error;

      // Parse API error from response body
      if (error.error && isApiError(error.error)) {
        enhancedError.apiError = error.error;
      }

      // Don't redirect on 401 for profile/auth-related API calls to avoid redirect loops
      const isProfileRequest = req.url.includes('/api/me');

      if (error.status === 401 && !isProfileRequest) {
        router.navigate(['/login']);
      } else if (error.status === 403) {
        router.navigate(['/forbidden']);
      } else if (error.status === 500) {
        console.error('Server error:', enhancedError.apiError?.message || error.message);
      }

      return throwError(() => enhancedError);
    })
  );
};