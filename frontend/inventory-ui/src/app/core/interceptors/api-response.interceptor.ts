import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';

export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {

  return next(req).pipe(

    map(event => {

      if (event instanceof HttpResponse) {

        const body: any = event.body;

        if (body?.success !== undefined && body?.data !== undefined) {

          return event.clone({
            body: body.data
          });

        }
      }

      return event;

    })

  );

};