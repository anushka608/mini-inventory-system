import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

/**
 * GLOBAL ERROR HANDLER
 * Handles backend ApiResponse errors + network errors
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  return next(req).pipe(

    catchError((error: HttpErrorResponse) => {

      let message = 'Unexpected error';

      if (error.error?.message) {
        message = error.error.message;
      }
      else if (error.status === 0) {
        message = 'Server unreachable';
      }
      else if (error.status >= 500) {
        message = 'Server error occurred';
      }

      console.error('API ERROR:', message);

      alert(message); // later replace with snackbar

      return throwError(() => error);
    })
  );
};