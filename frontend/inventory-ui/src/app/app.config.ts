import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { apiResponseInterceptor } from './core/interceptors/api-response.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';

/*
GLOBAL APP CONFIG (Standalone Angular)

Registers:
✔ HttpClient
✔ API response unwrap interceptor
✔ Global error handler
*/

export const appConfig: ApplicationConfig = {
  providers: [

    provideHttpClient(
      withInterceptors([
        apiResponseInterceptor,
        errorInterceptor
      ])
    )

  ]
};