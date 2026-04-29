import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let message = 'Something went wrong';

      if (error.status === 401) {
        message = 'Session expired. Please login again.';
        localStorage.clear();
        router.navigate(['/login']);
      } 
      else if (error.status === 403) {
        message = 'You are not authorized to perform this action';
      } 
      else if (error.status === 404) {
        // This handles the "No value present" error from your backend
        message = error.error?.message || 'The requested resource was not found';
      }
      else if (error.error?.message) {
        message = error.error.message;
      }

      snackBar.open(message, 'Close', {
        duration: 4000,
        verticalPosition: 'top',
        panelClass: ['error-snackbar'] // Optional: for custom styling
      });

      return throwError(() => error);
    })
  );
};