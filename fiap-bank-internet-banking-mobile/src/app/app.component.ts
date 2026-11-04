import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AutenticacaoService } from './services/autenticacao.service';
import { CreditoService } from './services/credito.service';
import { ClienteCoreInfo, SolicitacaoCreditoResponse, TransacaoExtrato } from './models/credito.model';
import { UsuarioLogado } from './models/autenticacao.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  // Área rolável das abas, que volta ao topo quando a aba muda
  @ViewChild('corpoApp') private corpoApp?: ElementRef<HTMLElement>;

  activeTab: 'home' | 'credito' | 'extrato' | 'historico' | 'core' = 'home';
  hideBalance: boolean = false;
  loading: boolean = false;
  feedbackMessage: string | null = null;
  feedbackType: 'success' | 'warning' | 'error' = 'success';

  // Estado do login
  logado: boolean = false;
  loginInformado: string = '';
  senhaInformada: string = '';

  // Customer State
  cpfCliente: string = '';
  nomeCliente: string = '';
  rendaMensal: number = 7500;

  // Core Bank Account Info
  contaInfo: ClienteCoreInfo = {
    statusConta: 'ATIVA',
    saldo: 12500.00,
    agencia: '0001',
    numeroConta: '12345-6'
  };
  private readonly contaPadrao: ClienteCoreInfo = { ...this.contaInfo };

  // Form Credit Request State
  selectedTipoCredito: string = 'EMPRÉSTIMO PESSOAL';
  valorSolicitado: number = 15000;
  quantidadeParcelas: number = 24;

  // Credit Types List
  tiposCredito = [
    { id: 'EMPRÉSTIMO PESSOAL', nome: 'Empréstimo Pessoal', taxa: '1,49% a.m.', icone: 'fa-user-tag' },
    { id: 'CRÉDITO IMOBILIÁRIO', nome: 'Crédito Imobiliário', taxa: '0,85% a.m.', icone: 'fa-house-chimney' },
    { id: 'CRÉDITO AUTOMOTIVO', nome: 'Financiamento Auto', taxa: '1,19% a.m.', icone: 'fa-car' },
    { id: 'CONSIGNADO', nome: 'Consignado FIAP', taxa: '0,99% a.m.', icone: 'fa-file-signature' }
  ];

  // Solicitacoes List
  solicitacoes: SolicitacaoCreditoResponse[] = [];

  // Extrato de Movimentacoes
  filtroExtrato: 'TODAS' | 'ENTRADAS' | 'SAIDAS' = 'TODAS';
  transacoes: TransacaoExtrato[] = [
    {
      id: 'TX-901',
      descricao: 'Pix Recebido - Maria Santos',
      tipo: 'ENTRADA',
      categoria: 'PIX',
      valor: 2500.00,
      data: new Date(Date.now() - 3600000 * 2).toISOString(),
      icone: 'fa-qrcode text-emerald'
    },
    {
      id: 'TX-902',
      descricao: 'Pagamento Fatura FIAP Card',
      tipo: 'SAIDA',
      categoria: 'PAGAMENTO',
      valor: 450.00,
      data: new Date(Date.now() - 3600000 * 18).toISOString(),
      icone: 'fa-credit-card text-danger'
    },
    {
      id: 'TX-903',
      descricao: 'Crédito Pessoal Aprovado FIAP Bank',
      tipo: 'ENTRADA',
      categoria: 'CREDITO',
      valor: 15000.00,
      data: new Date(Date.now() - 3600000 * 48).toISOString(),
      icone: 'fa-hand-holding-dollar text-primary'
    },
    {
      id: 'TX-904',
      descricao: 'Transferência Enviada - João Pereira',
      tipo: 'SAIDA',
      categoria: 'TRANSFERENCIA',
      valor: 120.50,
      data: new Date(Date.now() - 3600000 * 72).toISOString(),
      icone: 'fa-arrow-up-right-from-square text-amber'
    }
  ];
  private readonly transacoesPadrao: TransacaoExtrato[] = [...this.transacoes];

  // Recebe o serviço de crédito e o de autenticação
  constructor(
    private creditoService: CreditoService,
    private autenticacaoService: AutenticacaoService
  ) {}

  // Reabre a sessão salva se o token ainda vale; se não, limpa o que sobrou no localStorage
  ngOnInit(): void {
    const usuario = this.autenticacaoService.usuarioAtual();
    if (usuario && this.autenticacaoService.estaLogado()) {
      this.iniciarSessao(usuario);
    } else {
      this.autenticacaoService.encerrarSessao();
    }
  }

  // Valida os campos, autentica e só deixa entrar quem tem perfil CLIENTE
  entrar(): void {
    if (!this.loginInformado.trim() || !this.senhaInformada) {
      this.feedbackType = 'error';
      this.feedbackMessage = 'Informe seu CPF e sua senha.';
      return;
    }

    this.loading = true;
    this.feedbackMessage = null;

    this.autenticacaoService.autenticar(this.loginInformado, this.senhaInformada).subscribe({
      next: (resposta) => {
        this.loading = false;
        this.senhaInformada = '';
        if (!resposta.perfis.includes('CLIENTE')) {
          this.autenticacaoService.encerrarSessao();
          this.feedbackType = 'error';
          this.feedbackMessage = 'Este acesso é só para correntistas.';
          return;
        }
        this.loginInformado = '';
        this.iniciarSessao(resposta);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.feedbackType = 'error';
        this.feedbackMessage = err.status === 401
          ? 'Login ou senha inválidos.'
          : 'Erro ao entrar na Plataforma de Crédito. Verifique se o servidor backend está online.';
        console.error('Erro no login:', err);
      }
    });
  }

  // Preenche nome, CPF e conta com o usuário do token e carrega os dados da Plataforma
  private iniciarSessao(usuario: Pick<UsuarioLogado, 'nome' | 'cpf' | 'conta'>): void {
    this.cpfCliente = usuario.cpf;
    this.nomeCliente = usuario.nome;
    this.contaInfo = { ...this.contaPadrao, numeroConta: usuario.conta };
    this.logado = true;
    this.carregarDadosCore();
    this.carregarSolicitacoes();
  }

  // Encerra a sessão e limpa os dados do cliente antes do próximo login
  sair(): void {
    this.autenticacaoService.encerrarSessao();
    this.logado = false;
    this.loading = false;
    this.feedbackMessage = null;
    this.activeTab = 'home';
    this.cpfCliente = '';
    this.nomeCliente = '';
    this.rendaMensal = 7500;
    this.selectedTipoCredito = 'EMPRÉSTIMO PESSOAL';
    this.valorSolicitado = 15000;
    this.quantidadeParcelas = 24;
    this.contaInfo = { ...this.contaPadrao };
    this.solicitacoes = [];
    this.transacoes = [...this.transacoesPadrao];
  }

  // Trata 401 e 403 da Plataforma; devolve true quando o erro já foi resolvido aqui
  private tratarErroDeAcesso(err: HttpErrorResponse): boolean {
    if (err.status === 401) {
      this.sair();
      this.feedbackType = 'error';
      this.feedbackMessage = 'Sessão expirada, entre de novo.';
      return true;
    }
    if (err.status === 403) {
      this.feedbackType = 'error';
      this.feedbackMessage = 'Você só pode consultar o seu próprio CPF.';
      return true;
    }
    return false;
  }

  carregarDadosCore(): void {
    this.creditoService.consultarCoreBancario(this.cpfCliente).subscribe({
      next: (info) => {
        if (info && info.saldo !== undefined) {
          this.contaInfo = info;
        }
      },
      error: (err: HttpErrorResponse) => {
        if (!this.tratarErroDeAcesso(err)) {
          console.error('Erro ao consultar dados no Core Bancário via Plataforma:', err);
        }
      }
    });
  }

  carregarSolicitacoes(): void {
    this.loading = true;
    this.creditoService.consultarStatus(this.cpfCliente).subscribe({
      next: (data) => {
        this.solicitacoes = data || [];
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.solicitacoes = [];
        this.loading = false;
        if (!this.tratarErroDeAcesso(err)) {
          console.error('Erro ao buscar histórico de solicitações:', err);
        }
      }
    });
  }

  get transacoesFiltradas(): TransacaoExtrato[] {
    if (this.filtroExtrato === 'ENTRADAS') {
      return this.transacoes.filter(t => t.tipo === 'ENTRADA');
    }
    if (this.filtroExtrato === 'SAIDAS') {
      return this.transacoes.filter(t => t.tipo === 'SAIDA');
    }
    return this.transacoes;
  }

  get totalEntradas(): number {
    return this.transacoes
      .filter(t => t.tipo === 'ENTRADA')
      .reduce((acc, t) => acc + t.valor, 0);
  }

  get totalSaidas(): number {
    return this.transacoes
      .filter(t => t.tipo === 'SAIDA')
      .reduce((acc, t) => acc + t.valor, 0);
  }

  get parcelaEstimada(): number {
    if (!this.quantidadeParcelas || this.quantidadeParcelas <= 0) return 0;
    const taxaMes = this.getTaxaDecimal();
    const pv = this.valorSolicitado;
    const n = this.quantidadeParcelas;
    if (taxaMes === 0) return pv / n;
    return (pv * taxaMes * Math.pow(1 + taxaMes, n)) / (Math.pow(1 + taxaMes, n) - 1);
  }

  get totalPagar(): number {
    return this.parcelaEstimada * this.quantidadeParcelas;
  }

  getTaxaDecimal(): number {
    switch (this.selectedTipoCredito) {
      case 'CRÉDITO IMOBILIÁRIO': return 0.0085;
      case 'CRÉDITO AUTOMOTIVO': return 0.0119;
      case 'CONSIGNADO': return 0.0099;
      default: return 0.0149;
    }
  }

  solicitarCredito(): void {
    if (!this.valorSolicitado || this.valorSolicitado <= 0) {
      this.feedbackType = 'error';
      this.feedbackMessage = 'Por favor, informe um valor desejado válido para o crédito.';
      return;
    }

    this.loading = true;
    this.feedbackMessage = null;

    const payload = {
      cpfCliente: this.cpfCliente,
      nomeCliente: this.nomeCliente,
      tipoCredito: this.selectedTipoCredito,
      valorSolicitado: this.valorSolicitado,
      quantidadeParcelas: this.quantidadeParcelas,
      rendaMensal: this.rendaMensal
    };

    this.creditoService.solicitarCredito(payload).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.status === 'APROVADO') {
          this.feedbackType = 'success';
          this.feedbackMessage = `Crédito de R$ ${this.valorSolicitado.toLocaleString('pt-BR')} APROVADO com sucesso!`;
          
          // Adiciona lancamento no extrato
          this.transacoes.unshift({
            id: `TX-${Math.floor(Math.random() * 900) + 100}`,
            descricao: `${this.nomeDoTipo(res.tipoCredito)} Aprovado`,
            tipo: 'ENTRADA',
            categoria: 'CREDITO',
            valor: res.valorSolicitado,
            data: new Date().toISOString(),
            icone: 'fa-hand-holding-dollar text-primary'
          });
          this.contaInfo.saldo += res.valorSolicitado;
        } else if (res.status === 'EM_ANALISE') {
          this.feedbackType = 'warning';
          this.feedbackMessage = `Sua solicitação de R$ ${this.valorSolicitado.toLocaleString('pt-BR')} foi encaminhada para a Mesa de Operações.`;
        } else {
          this.feedbackType = 'error';
          this.feedbackMessage = `Solicitação não aprovada: ${res.parecerOperador}`;
        }
        this.carregarSolicitacoes();
        setTimeout(() => {
          this.activeTab = 'historico';
          this.voltarAoTopo();
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        if (this.tratarErroDeAcesso(err)) {
          return;
        }
        this.feedbackType = 'error';
        this.feedbackMessage = 'Erro ao processar solicitação na Plataforma de Crédito. Verifique se o servidor backend está online.';
        console.error('Erro na solicitação de crédito:', err);
      }
    });
  }

  // Nome do tipo de crédito como aparece nos botões da solicitação
  private nomeDoTipo(tipo: string): string {
    return this.tiposCredito.find(t => t.id === tipo)?.nome ?? tipo;
  }

  // Categoria do extrato escrita com acento, como aparece na tela
  nomeDaCategoria(categoria: TransacaoExtrato['categoria']): string {
    const nomes: Record<TransacaoExtrato['categoria'], string> = {
      PIX: 'PIX',
      CREDITO: 'CRÉDITO',
      TRANSFERENCIA: 'TRANSFERÊNCIA',
      PAGAMENTO: 'PAGAMENTO'
    };
    return nomes[categoria];
  }

  selectTipo(tipo: string): void {
    this.selectedTipoCredito = tipo;
  }

  toggleBalanceVisibility(): void {
    this.hideBalance = !this.hideBalance;
  }

  setTab(tab: 'home' | 'credito' | 'extrato' | 'historico' | 'core'): void {
    this.activeTab = tab;
    this.voltarAoTopo();
    if (tab === 'historico') {
      this.carregarSolicitacoes();
    }
  }

  // Rola a área das abas para o topo, para a aba nova abrir do começo
  private voltarAoTopo(): void {
    this.corpoApp?.nativeElement.scrollTo({ top: 0 });
  }
}
