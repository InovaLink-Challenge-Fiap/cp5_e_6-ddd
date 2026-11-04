# FIAP Bank - Internet Banking Mobile 📱

Aplicação Web Mobile responsiva (**Mobile-First**) do cliente final do **FIAP Bank**, desenvolvida em **Angular 17+**. 

Permite que os clientes consultem seu saldo e dados da conta, simulem propostas de crédito em tempo real (Empréstimo Pessoal, Financiamento Imobiliário, Crédito Automotivo e Consignado), enviem propostas para a esteira da Plataforma de Crédito e acompanhem o status de suas solicitações.

---

## 🛠️ Tecnologias Utilizadas

- **Angular 17.3+** (Standalone Components)
- **TypeScript 5.4+**
- **RxJS 7.8+**
- **HTML5 & CSS3** (layout responsivo com estética premium e fluida para smartphone)
- **Node.js / npm**

---

## 📋 Pré-requisitos

- **Node.js** (versão 18.x LTS ou 20.x LTS recomendada).
- **npm** (gerenciador de pacotes, incluído com o Node.js).
- Serviço **Plataforma de Crédito** em execução na porta `8081` (obrigatório para o login).

---

## 🚀 Como Executar a Aplicação

### 1. Instalar as dependências

Navegue até o diretório do projeto e execute:

```bash
cd fiap-bank-internet-banking-mobile
npm install
```

### 2. Iniciar o servidor de desenvolvimento

Execute o comando:

```bash
npm run start
```
*(ou se possuir o Angular CLI globalmente: `ng serve`)*

A aplicação estará disponível em: **`http://localhost:4200`**

O servidor inclui suporte a *Hot Reloading*, atualizando automaticamente a página ao salvar alterações no código.

---

## ⚙️ Configuração de Endpoints

As URLs da **Plataforma de Crédito** são configuradas em:

1. **Tempo de Execução (Runtime):** `src/assets/config/app-config.json`
   ```json
   {
     "plataformaCreditoUrl": "http://localhost:8081/api/credito",
     "autenticacaoUrl": "http://localhost:8081/api/auth"
   }
   ```
2. **Tempo de Compilação (Build):** `src/environments/environment.ts` (desenvolvimento) e `src/environments/environment.prod.ts` (produção).

---

## ⚙️ Login e Segurança

A Plataforma de Crédito é um **OAuth2 Resource Server com JWT**: todas as rotas usadas pelo app exigem um token de um usuário com perfil `CLIENTE`, e o cliente só pode operar o próprio CPF. Por isso o app abre na tela de login.

### Usuários de teste

| Login (CPF) | Senha | Cliente | Conta |
| :--- | :--- | :--- | :--- |
| `12345678900` | `123456` | Ana Silva | 12345-6 |
| `98765432100` | `123456` | Carlos Eduardo Oliveira | 78901-2 |
| `55544433399` | `123456` | Mariana Souza | 33333-3 |
| `11122233347` | `123456` | Bruno Ferreira | 44444-7 |

Os usuários `operador` / `operador123` e `admin` / `admin123` são da Mesa de Operações. Eles autenticam, mas o app recusa a entrada com a mensagem *"Este acesso é só para correntistas."*.

### Fluxo do token

```
Tela de login
   -> POST /api/auth/login (sem Authorization)
   -> accessToken guardado no localStorage ('access_token')
   -> AuthInterceptor lê o token a cada requisição
   -> Authorization: Bearer <token> nas chamadas à Plataforma
```

- `src/app/services/autenticacao.service.ts`: o `AutenticacaoService` faz o login e guarda `access_token` e `usuario` (nome, CPF, conta, perfis e `expiraEm`) no `localStorage`.
- `src/app/models/autenticacao.model.ts`: tipos `LoginResponse` (resposta do login) e `UsuarioLogado` (dados salvos da sessão).
- `src/app/interceptadores/auth.interceptor.ts`: `HttpInterceptor` que anexa o Bearer nas chamadas à Plataforma de Crédito, exceto no login.

### Expiração e erros

- O token vale **2 horas** (`expiresIn: 7200`) e não é renovado. Depois disso é preciso entrar de novo.
- **401** em qualquer chamada: o app limpa a sessão e volta ao login com a mensagem *"Sessão expirada, entre de novo"*.
- **403** (CPF diferente do token): o app mostra *"Você só pode consultar o seu próprio CPF"*.
- O botão **Sair** fica no fim da aba **Core Bank** e limpa a sessão.

---

## 📱 Funcionalidades da Aplicação

- **Início (Home):** Visão geral do saldo em conta, atalhos rápidos e resumo dos produtos disponíveis.
- **Simulador e Solicitação de Crédito:**
  - Escolha do tipo de linha de crédito (*Pessoal, Imobiliário, Automotivo, Consignado*).
  - Ajuste dinâmico de valor e quantidade de parcelas com cálculo automático da parcela estimada e taxas.
  - Submissão da proposta para análise na esteira da Plataforma de Crédito via chamada HTTP POST.
- **Histórico de Solicitações:** Consulta em tempo real de todas as propostas vinculadas ao CPF do cliente, com badges de status (*Aprovado, Em Análise, Reprovado*) e pareceres.
- **Extrato Bancário:** Visualização dos lançamentos e movimentações de crédito/débito.
- **Consulta ao Core:** Visualização das informações retornadas diretamente do Core Bancário via integração Feign.

---

## ⚙️ Ajustes de Layout

O HTML das telas usa classes utilitárias do Bootstrap (`d-flex`, `justify-content-between`, `mb-3`, `me-2`, `text-danger`, entre outras), mas o Bootstrap nunca fez parte do app. Sem essas classes, os itens ficavam colados, o mínimo e o máximo do valor apareciam juntos, as saídas não ficavam vermelhas e o botão do olho no Core Bank aparecia com o estilo padrão do navegador.

Instalar o Bootstrap mudaria o visual do app inteiro (fontes, botões e margens). Por isso, só as classes que o HTML usa foram criadas no `src/styles.css`, com as cores do próprio app. O fim do `app.component.css` ganhou um bloco de ajustes:

| Tela | Ajuste |
| :--- | :--- |
| Início | Últimas movimentações com título e valor lado a lado, como no layout original, em fonte um pouco menor para o título caber inteiro na linha, e ícone de largura fixa |
| Crédito | Rótulo e selo do valor desejado na mesma linha |
| Extrato | Movimentações no mesmo padrão da tela inicial, com título e valor na mesma linha |
| Status | Texto da busca por CPF alinhado à esquerda, já que o campo não tem ícone, e aviso de lista vazia centralizado |
| Core Bank | Botão do olho redondo, no formato do botão de perfil, e saldo em um tamanho que cabe na linha do rótulo |
| Todas | Ao trocar de aba, a tela abre do topo em vez de herdar a rolagem da aba anterior; depois de enviar uma solicitação, o Status já mostra a nova primeiro |

Em telas maiores que um celular, a moldura acompanha a altura da janela, entre 480 e 840 pixels, para a barra de navegação de baixo não sair da tela quando a janela é mais baixa ou o navegador está com zoom.

Subtítulos e pareceres, que são textos corridos, quebram linha sem deixar uma palavra sozinha no fim. Os textos das telas também foram padronizados: taxas com vírgula (`1,49% a.m.`), categorias do extrato exibidas com acento (`CRÉDITO`, `TRANSFERÊNCIA`) e o lançamento de crédito aprovado com o nome do tipo (`Consignado FIAP Aprovado`).

---

## 📦 Build para Produção

Para gerar os artefatos estáticos otimizados para produção:

```bash
npm run build
```

Os arquivos compilados serão gerados no diretório `dist/fiap-bank-internet-banking-mobile/`.

---

## 🧪 Executar Testes Unitários

Para rodar os testes unitários com Karma e Jasmine:

```bash
npm test
```
