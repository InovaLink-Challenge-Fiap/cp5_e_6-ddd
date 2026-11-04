import { ApplicationConfig, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { routes } from './app.routes';
import { AppConfigService } from './services/app-config.service';
import { AuthInterceptor } from './interceptadores/auth.interceptor';

export function initAppConfig(appConfigService: AppConfigService) {
  return () => appConfigService.loadAppConfig();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor, // anexa o token JWT nas chamadas para a Plataforma de Crédito
      multi: true
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initAppConfig,
      deps: [AppConfigService],
      multi: true
    }
  ]
};
