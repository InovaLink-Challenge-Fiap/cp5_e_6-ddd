import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse, UsuarioLogado } from '../models/autenticacao.model';
import { AppConfigService } from './app-config.service';

// Login na Plataforma de Crédito e sessão do cliente guardada no localStorage
@Injectable({
  providedIn: 'root'
})
export class AutenticacaoService {
  private readonly chaveToken = 'access_token';
  private readonly chaveUsuario = 'usuario';

  // Recebe o HttpClient e a configuração com as URLs da Plataforma
  constructor(
    private http: HttpClient,
    private appConfigService: AppConfigService
  ) {}

  // Faz o login e, quando dá certo, já guarda o token e os dados do cliente
  autenticar(login: string, senha: string): Observable<LoginResponse> {
    const corpo = { login: this.normalizarLogin(login), senha };
    return this.http.post<LoginResponse>(`${this.appConfigService.autenticacaoUrl}/login`, corpo).pipe(
      tap((resposta) => this.guardarSessao(resposta))
    );
  }

  // Só conta como logado se houver token e ele ainda não tiver vencido
  estaLogado(): boolean {
    const usuario = this.usuarioAtual();
    return !!localStorage.getItem(this.chaveToken) && !!usuario && usuario.expiraEm > Date.now();
  }

  // Lê o cliente salvo no localStorage; devolve null se não houver ou se o JSON estiver quebrado
  usuarioAtual(): UsuarioLogado | null {
    const salvo = localStorage.getItem(this.chaveUsuario);
    if (!salvo) {
      return null;
    }
    try {
      return JSON.parse(salvo) as UsuarioLogado;
    } catch {
      return null;
    }
  }

  // Remove o token e os dados do cliente do localStorage
  encerrarSessao(): void {
    localStorage.removeItem(this.chaveToken);
    localStorage.removeItem(this.chaveUsuario);
  }

  // Guarda o token e calcula quando ele vence a partir do expiresIn em segundos
  private guardarSessao(resposta: LoginResponse): void {
    const usuario: UsuarioLogado = {
      nome: resposta.nome,
      cpf: resposta.cpf,
      conta: resposta.conta,
      perfis: resposta.perfis,
      expiraEm: Date.now() + resposta.expiresIn * 1000
    };
    localStorage.setItem(this.chaveToken, resposta.accessToken);
    localStorage.setItem(this.chaveUsuario, JSON.stringify(usuario));
  }

  // A rota de login só aceita o CPF sem máscara
  private normalizarLogin(login: string): string {
    const texto = login.trim();
    return /^[\d.\-\s]+$/.test(texto) ? texto.replace(/\D/g, '') : texto;
  }
}
