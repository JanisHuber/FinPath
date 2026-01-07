import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = (() => {
    try {
      return localStorage.getItem('token');
    } catch (e) {
      console.error('localStorage not available', e);
      return null;
    }
  })();

  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(authReq);
};
