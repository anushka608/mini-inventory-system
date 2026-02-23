import { HttpInterceptorFn } from '@angular/common/http';
import { map } from 'rxjs/operators';

/*
This interceptor automatically unwraps backend ApiResponse<T>

Before:
{ success:true, message:'', data:[...] }

After interceptor:
[...]

services now receive clean data directly.
*/

export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {

  return next(req).pipe(

    map((event: any) => {

      /* only unwrap if it looks like ApiResponse */
      if (event?.body?.success !== undefined && event?.body?.data !== undefined) {

        return event.clone({
          body: event.body.data
        });
      }

      return event;
    })

  );
};