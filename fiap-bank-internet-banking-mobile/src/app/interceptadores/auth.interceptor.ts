import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppConfigService } from '../services/app-config.service';

// Anexa o Bearer do localStorage nas requisições para a Plataforma de Crédito
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  // Recebe a configuração com a URL da Plataforma
  constructor(private appConfigService: AppConfigService) {}

  // Com token salvo e chamada para a Plataforma, clona a requisição com o header Authorization
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem('access_token');
    if (token && this.levaToken(request.url)) {
      const requisicaoComToken = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(requisicaoComToken);
    }
    return next.handle(request);
  }

  // Só as chamadas para o servidor da Plataforma levam o token; o login não
  private levaToken(url: string): boolean {
    const base = this.appConfigService.plataformaCreditoUrl;
    const origem = base.startsWith('http') ? new URL(base).origin : location.origin;
    return new URL(url, location.href).origin === origem && !url.includes('/api/auth/login');
  }
}
