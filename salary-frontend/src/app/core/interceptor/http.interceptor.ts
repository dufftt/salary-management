import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, throwError } from 'rxjs';
import { LoadingService } from '../services/loading.service';
import { MatSnackBar } from '@angular/material/snack-bar';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  const snackBar = inject(MatSnackBar);

  // Show loading spinner
  loadingService.show();

  return next(req).pipe(
    finalize(() => {
      // Hide loading spinner when request completes (success or error)
      loadingService.hide();
    }),
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Something went wrong. Please try again.';

      if (error.error) {
        // Backend structured error (our ErrorResponse)
        if (error.error.message) {
          errorMessage = error.error.message;
        }

        // Validation errors
        if (error.error.validationErrors) {
          const validationMessages = Object.values(error.error.validationErrors).join(', ');
          errorMessage = validationMessages || errorMessage;
        }
      } else if (error.status === 0) {
        errorMessage = 'Unable to connect to the server.';
      } else if (error.status === 404) {
        errorMessage = 'Resource not found.';
      } else if (error.status === 409) {
        errorMessage = error.error?.message || 'Conflict occurred.';
      }

      // Show error notification
      snackBar.open(errorMessage, 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar'],
        horizontalPosition: 'end',
        verticalPosition: 'top'
      });

      return throwError(() => error);
    })
  );
};