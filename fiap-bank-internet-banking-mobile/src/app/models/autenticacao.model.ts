// Resposta de POST /api/auth/login da Plataforma de Crédito
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  nome: string;
  cpf: string;
  conta: string;
  perfis: string[];
}

// Dados do cliente guardados no localStorage; expiraEm é o horário em ms em que o token vence
export interface UsuarioLogado {
  nome: string;
  cpf: string;
  conta: string;
  perfis: string[];
  expiraEm: number;
}
