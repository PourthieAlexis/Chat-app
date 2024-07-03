import {
  HttpEvent,
  HttpHandlerFn,
  HttpHeaders,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Observable, throwError } from 'rxjs';
import { Router } from '@angular/router';

export function authInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();

  if (!token) {
    return next(req);
  }

  if (auth.isTokenExpired(token)) {
    auth.removeToken();
    router.navigate(['/login']);
    return throwError(() => new Error('Token expired'));
  }

  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`,
  });

  const newReq = req.clone({
    headers,
  });

  return next(newReq);
}
