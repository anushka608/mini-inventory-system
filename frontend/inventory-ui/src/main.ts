import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { App } from './app/app';
import { routes } from './app/app.routes';

import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { apiResponseInterceptor } from './app/core/interceptors/api-response.interceptor';
import { errorInterceptor } from './app/core/interceptors/error.interceptor';

bootstrapApplication(App, {
  providers: [

    provideRouter(routes),

    provideHttpClient(
      withInterceptors([
        apiResponseInterceptor,
        errorInterceptor
      ])
    )

  ]
}).catch(err => console.error(err));