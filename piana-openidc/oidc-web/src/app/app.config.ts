import { APP_INITIALIZER, ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';

import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { LocalStorageService } from './component/service/local-storage/local-storage.service';
import { AuthService } from './component/auth/auth.service';
import { authInterceptor } from './component/auth/http-auth-interceptor';

function appInitializer(authService: AuthService) {
  return async () => {
    console.log('appInitializer')
    await authService.refreshAuthState();
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withHashLocation()),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializer,
      deps: [AuthService, LocalStorageService],
      multi: true,
    }
  ]
};
