# FIAP Bank - Plataforma de Crédito 💳

Microsserviço de **Esteira e Motor de Decisão de Crédito** do FIAP Bank. Responsável por processar propostas de crédito, avaliar regras de negócio e risco, comunicar-se com o Core Bancário via **Spring Cloud OpenFeign**, simular verificações no SCR (Banco Central), no Bureau de Crédito (Serasa/SPC) e no CRM corporativo, e notificar clientes.

O projeto segue **Domain-Driven Design (DDD)** em multi-módulo Maven e expõe uma API RESTful em `/api/v1`. As rotas antigas em `/api/credito` continuam disponíveis por um adaptador, usado pela Mesa de Operações e pelo app mobile. A API é protegida como **OAuth2 Resource Server** com JWT.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1**
- **Spring Cloud 2025.1.4** com **OpenFeign** (comunicação declarativa com o Core Bancário)
- **Spring Security 7** e **OAuth2 Resource Server** (autenticação stateless com JWT)
- **Spring Data JPA**
- **Bean Validation**
- **Lombok** (fora do `domain`)
- **Spring AOP com AspectJ**
- **Spring Boot Actuator**
- **H2 Database** (em memória)
- **Maven** (projeto multi-módulo)

---

## 📁 Estrutura dos Módulos

As dependências seguem a hierarquia do enunciado: `presentation` enxerga `application`; `application` enxerga `infrastructure` e `domain`; `infrastructure` enxerga `domain`; e `domain` não depende de nenhum. O `presentation` compila só contra o `application`; `domain` e `infrastructure` entram nele só em tempo de execução (escopo `runtime`). O `maven-enforcer-plugin` barra no build qualquer dependência fora dessa ordem.

| Módulo | Responsabilidade |
| :--- | :--- |
| `fiap-bank-plataforma-credito-domain` | Agregado `SolicitacaoCredito`, `Cpf`, `Usuario`, `MotorDeCredito`, portas de repositório e de integração e exceções. Java puro, sem Spring, JPA ou Lombok. |
| `fiap-bank-plataforma-credito-infrastructure` | Entidades e repositórios JPA, cliente Feign do Core Bancário e as simulações de SCR, Bureau, CRM e notificação. |
| `fiap-bank-plataforma-credito-application` | Casos de uso, DTOs com Bean Validation, porta `CodificadorSenha`, exceções da aplicação, `TradutorDeExcecoes` e carga inicial. |
| `fiap-bank-plataforma-credito-presentation` | Controllers REST, adaptador legado, segurança (Resource Server, JWT e login), tratamento de erros, `application.yml` e classe principal. |

---

## 📌 Diagnóstico do Estado Inicial

No código de partida (commit `f27aeee`), a Plataforma era um único módulo Spring Boot. O que estava errado e o que foi feito:

| Problema | Ajuste |
| :--- | :--- |
| Módulo único, sem fronteira entre camadas: controller, regra de negócio, entidade JPA e cliente Feign no mesmo projeto | Quatro módulos Maven, com o `maven-enforcer-plugin` conferindo as dependências |
| O modelo de negócio era a entidade JPA, com setter público em todos os campos | Agregado `SolicitacaoCredito` em Java puro, sem setters, validando os dados na criação e na mudança de status |
| `PlataformaCreditoService` misturava a regra de crédito com o acesso ao banco, a chamada ao Core e a notificação | Regra no `MotorDeCredito`; persistência e integrações atrás de portas do domínio |
| CPF como texto solto, cada classe tirava a máscara do seu jeito | Objeto de valor `Cpf`, que valida e normaliza em um lugar só |
| Qualquer erro do Core devolvia uma conta falsa fixa | `ContaNaoEncontradaException` (`404`) ou `CoreBancarioIndisponivelException` (`503`) |
| A conta do Core era consultada, mas o resultado não entrava na análise | A solicitação só segue se o CPF tiver conta no Core (senão `404`) e se ela estiver `ATIVA` (senão `409`) |
| Aprovar e reprovar não conferiam o status: dava para aprovar uma solicitação já reprovada | Só solicitação `EM_ANALISE` muda de status; fora disso a resposta é `409` |
| Rotas com verbo na URI, tudo em `POST` e sempre `200` | API RESTful em `/api/v1`; as rotas antigas ficaram no `CreditoLegadoController` |
| Sem validação de entrada e sem tratamento de erros: solicitação inexistente lançava `IllegalArgumentException` e virava `500` | Bean Validation nos DTOs e `GlobalExceptionHandler` respondendo em `ProblemDetail` (`404` para solicitação inexistente) |
| `@CrossOrigin(origins = "*")` no controller e nenhuma autenticação | CORS só para `http://localhost:*` e JWT em toda rota protegida (Checkpoint 6) |
| Spring Cloud `2024.0.0` no `pom.xml`, versão que não é compatível com o Spring Boot 4.1 e impedia a aplicação de subir; a Mesa tinha a mesma versão | Spring Cloud `2025.1.4`, a versão para o Spring Boot 4.1, na Plataforma e na Mesa |
| `spring.h2.console.enabled` ligado, mas sem a dependência `spring-boot-h2console`, que no Spring Boot 4 é separada: o `/h2-console` não abria | Dependência adicionada no `presentation` |
| A carga inicial usava os tipos `EMPRESTIMO_PESSOAL`, `CREDITO_IMOBILIARIO` e `CREDITO_AUTOMOTIVO`, diferentes dos que o app envia (`EMPRÉSTIMO PESSOAL`, por exemplo), e nenhuma solicitação ficava em análise para a Mesa decidir | Carga com os mesmos tipos do app e duas solicitações `EM_ANALISE` |
| O Core Bancário não existia | Core Bancário implementado no monorepo e consumido via OpenFeign |

---

## 📋 Pré-requisitos

- **Java JDK 21** instalado e configurado no `PATH`.
- **Apache Maven 3.9+** instalado.
- Serviço **Core Bancário** rodando na porta `8080`. Sem ele a aplicação sobe, mas as solicitações iniciais não são carregadas e as operações que dependem da conta do cliente respondem `503`.

---

## 🚀 Como Executar a Aplicação

### 1. Compilar o projeto

Na raiz do diretório `fiap-bank-plataforma-credito`:

```bash
mvn clean install
```

### 2. Iniciar a aplicação via Maven

```bash
mvn -pl fiap-bank-plataforma-credito-presentation spring-boot:run
```

### 3. Executar via arquivo JAR

```bash
java -jar fiap-bank-plataforma-credito-presentation/target/fiap-bank-plataforma-credito-presentation-1.0.0-SNAPSHOT.jar
```

A aplicação estará acessível em: **`http://localhost:8081`**

---

## ⚙️ Variáveis de Ambiente e Configuração

As seguintes propriedades podem ser customizadas no arquivo `fiap-bank-plataforma-credito-presentation/src/main/resources/application.yml` ou passadas como variáveis de ambiente:

| Propriedade | Variável de Ambiente | Valor Padrão | Descrição |
| :--- | :--- | :--- | :--- |
| `server.port` | `SERVER_PORT` | `8081` | Porta HTTP da aplicação |
| `core.bancario.url` | `CORE_BANCARIO_URL` | `http://localhost:8080/api/v1` | URL base da API do Core Bancário |

Os parâmetros do token (`jwt.*`) estão na seção de segurança.

---

## 🗄️ Console do Banco de Dados H2

- **URL:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:plataformacreditodb`
- **User Name:** `sa`
- **Password:** *(deixe em branco)*

O console não usa token: `/h2-console/**` é liberado na configuração de segurança.

---

## 🧪 Usuários e Dados Iniciais

Carregados ao iniciar a aplicação. Como o banco é em memória, voltam ao estado inicial a cada reinício.

### Usuários

| Login | Senha | Nome | CPF | Conta | Perfil |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `12345678900` | `123456` | Ana Silva | `12345678900` | `12345-6` | `CLIENTE` |
| `98765432100` | `123456` | Carlos Eduardo Oliveira | `98765432100` | `78901-2` | `CLIENTE` |
| `55544433399` | `123456` | Mariana Souza | `55544433399` | `33333-3` | `CLIENTE` |
| `11122233347` | `123456` | Bruno Ferreira | `11122233347` | `44444-7` | `CLIENTE` |
| `operador` | `operador123` | Carlos Ramos | | | `OPERADOR` |
| `admin` | `admin123` | Administrador | | | `ADMIN` |

### Solicitações

Passam pelo mesmo fluxo de uma solicitação real, por isso dependem do Core Bancário. Se ele estiver fora, cada falha é registrada no log e a aplicação continua subindo.

| ID | Cliente | Tipo | Valor | Parcelas | Renda | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | Ana Silva | `EMPRÉSTIMO PESSOAL` | 15.000,00 | 24 | 7.500,00 | `APROVADO` |
| 2 | Carlos Eduardo Oliveira | `CRÉDITO IMOBILIÁRIO` | 350.000,00 | 180 | 12.000,00 | `APROVADO` |
| 3 | Mariana Souza | `CRÉDITO AUTOMOTIVO` | 45.000,00 | 48 | 4.200,00 | `REPROVADO` |
| 4 | Ana Silva | `CRÉDITO AUTOMOTIVO` | 80.000,00 | 12 | 7.500,00 | `EM_ANALISE` |
| 5 | Bruno Ferreira | `EMPRÉSTIMO PESSOAL` | 6.000,00 | 12 | 5.000,00 | `EM_ANALISE` |

### Motor de crédito

O `MotorDeCredito` aplica as regras nesta ordem e para na primeira que se encaixa:

| Ordem | Regra | Resultado |
| :---: | :--- | :--- |
| 1 | Restrição ativa no SCR | `REPROVADO` |
| 2 | Score do Bureau abaixo de 500 | `REPROVADO` |
| 3 | Parcela estimada acima de 30% da renda informada | `EM_ANALISE` (vai para a Mesa) |
| 4 | Inadimplência no histórico do CRM | `EM_ANALISE` (vai para a Mesa) |
| 5 | Nenhuma das anteriores | `APROVADO` |

As regras 1, 2, 3 e 5 já existiam no código de partida e continuam iguais. O CRM não existia, mas o enunciado cita o histórico do cliente no CRM corporativo; ele entrou como a porta `CrmGateway`, no mesmo padrão do SCR e do Bureau, com a regra 4.

### Integrações simuladas

Só o Core Bancário é chamado de verdade (Feign). As outras integrações são simuladas no módulo `infrastructure` e respondem sempre igual para o mesmo CPF.

| Integração | Classe | Regra da simulação |
| :--- | :--- | :--- |
| SCR (Banco Central) | `ScrGatewayImpl` | CPF terminado em `9` tem restrição ativa e a solicitação é reprovada |
| Bureau de Crédito | `BureauCreditoGatewayImpl` | Score entre 400 e 950 calculado a partir dos dígitos do CPF; abaixo de 500 reprova |
| CRM corporativo | `CrmGatewayImpl` | CPF terminado em `7` tem inadimplência no histórico (regra 4 do Motor) |
| Notificações | `NotificacaoGatewayImpl` | Escreve a mensagem no console da aplicação |

---

## ⚙️ Segurança (OAuth2 Resource Server e JWT)

A Plataforma é um OAuth2 Resource Server sem sessão: toda rota protegida exige o cabeçalho `Authorization: Bearer <token>`. Apenas `/api/auth/**` e `/h2-console/**` são públicas; todo o resto, incluindo `/actuator/**`, responde `401` sem token.

As dependências `spring-boot-starter-security` e `spring-boot-starter-oauth2-resource-server` ficam só no `pom.xml` do `presentation`; os outros módulos não têm nada do Spring Security.

### Parâmetros do `application.yml`

| Propriedade | Valor | Descrição |
| :--- | :--- | :--- |
| `jwt.secret` | texto de 64 caracteres | Chave HMAC que assina e valida os tokens (HS256) |
| `jwt.algorithm` | `HmacSHA256` | Algoritmo da chave secreta |
| `jwt.expiration-seconds` | `7200` | Validade do token, em segundos |
| `jwt.issuer` | `fiap-bank-plataforma-credito` | Valor da claim `iss`; tokens de outro emissor são rejeitados |

### Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"12345678900","senha":"123456"}'
```

Resposta `200`. Para operador e admin, `cpf` e `conta` vêm `null`.

```json
{
  "accessToken": "eyJraWQiOi...",
  "tokenType": "Bearer",
  "expiresIn": 7200,
  "nome": "Ana Silva",
  "cpf": "12345678900",
  "conta": "12345-6",
  "perfis": ["CLIENTE"]
}
```

Campo vazio ou ausente responde `400`; login ou senha errados, `401`. As senhas ficam no banco com **BCrypt**.

### Usando o token

```bash
curl -X POST http://localhost:8081/api/credito/consultarStatusCredito \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"cpfCliente":"12345678900"}'
```

Claims do token (RFC 7519):

| Claim | Cliente | Operador / Admin |
| :--- | :--- | :--- |
| `iss` | `fiap-bank-plataforma-credito` | `fiap-bank-plataforma-credito` |
| `sub` | CPF | login |
| `iat`, `exp` | emissão e expiração | emissão e expiração |
| `roles` | `["CLIENTE"]` | `["OPERADOR"]` ou `["ADMIN"]` |
| `scope` | `ROLE_CLIENTE` | `ROLE_OPERADOR` ou `ROLE_ADMIN` |
| `cpf`, `conta` | CPF e número da conta | não existem |

### Permissões por endpoint

A regra fica no `@PreAuthorize` de cada método dos controllers. "Próprio CPF" é comparado com a claim `cpf` do token pelo componente `AcessoCliente`, com ou sem máscara.

| Rota | Quem pode acessar |
| :--- | :--- |
| `POST /api/auth/login` | Público |
| `/h2-console/**` | Público |
| `POST /api/v1/solicitacoes` | `CLIENTE` do próprio CPF |
| `GET /api/v1/solicitacoes` | `OPERADOR`, `ADMIN` |
| `GET /api/v1/solicitacoes/{id}` | `OPERADOR`, `ADMIN` |
| `PUT /api/v1/solicitacoes/{id}/status` | `OPERADOR`, `ADMIN` |
| `GET /api/v1/clientes/{cpf}/solicitacoes` | `OPERADOR`, `ADMIN`, `CLIENTE` do próprio CPF |
| `GET /api/v1/clientes/{cpf}/conta` | `OPERADOR`, `ADMIN`, `CLIENTE` do próprio CPF |
| `POST /api/credito/solicitarCredito` | `CLIENTE`, com `cpfCliente` igual ao do token |
| `POST /api/credito/consultarStatusCredito` | `OPERADOR`, `ADMIN`, `CLIENTE` do próprio CPF |
| `POST /api/credito/consultarClienteCore` | `OPERADOR`, `ADMIN`, `CLIENTE` do próprio CPF |
| `POST /api/credito/buscarSolicitacaoPorId` | `OPERADOR`, `ADMIN` |
| `POST /api/credito/listarTodasSolicitacoes` | `OPERADOR`, `ADMIN` |
| `POST /api/credito/aprovarSolicitacao` | `OPERADOR`, `ADMIN` |
| `POST /api/credito/reprovarSolicitacao` | `OPERADOR`, `ADMIN` |

### Erros 401 e 403

Saem no formato `ProblemDetail` (`application/problem+json`), como os demais erros da API.

| Status | Quando | `detail` |
| :--- | :--- | :--- |
| `401` | Token ausente, malformado, com assinatura inválida ou expirado (com o cabeçalho `WWW-Authenticate: Bearer`) | `Token de acesso ausente, invalido ou expirado` |
| `401` | Login com credenciais erradas | `Login ou senha invalidos` |
| `403` | Token válido sem permissão para a rota ou para aquele CPF | `Acesso negado: sem permissao para este recurso` |

```json
{
  "detail": "Acesso negado: sem permissao para este recurso",
  "instance": "/api/credito/listarTodasSolicitacoes",
  "status": 403,
  "title": "Forbidden"
}
```

### Decisões

- `@PreAuthorize` ficou só nos controllers, para não levar Spring Security para o `application`.
- O token do cliente segue o enunciado, com `scope` fixo em `ROLE_CLIENTE`. Para operador e admin o `scope` vem do perfil (`ROLE_OPERADOR`, `ROLE_ADMIN`), senão todo token passaria por cliente.
- Operador e admin não têm CPF nem conta, então o `sub` deles é o login e essas claims ficam de fora.
- O `SecurityConfig` libera CORS para `http://localhost:*`, porque a chamada `OPTIONS` do app chega sem token.
- O `SecurityConfig` também registra o `JwtEncoder` que o `TokenService` usa, com a mesma chave HS256.
- `/h2-console/**` é público e o `frameOptions` fica desligado, porque o console abre em frames.
- O `TratadorDeErrosDeSeguranca` repassa os erros dos filtros ao `GlobalExceptionHandler`, que monta o `ProblemDetail`.
- Em `/api/v1/clientes/{cpf}/...`, CPF malformado responde `403` para cliente, porque não bate com o CPF do token, e `400` para operador e admin. Nas rotas legadas, que recebem o CPF no corpo, o Bean Validation vem antes e responde `400` para todos.
- `accessToken`, `tokenType` e `expiresIn` são os campos de resposta do OAuth2 (`access_token`, `token_type` e `expires_in`) escritos em camelCase, por isso ficaram em inglês.

---

## 🌐 API RESTful

Base URL: `http://localhost:8081/api/v1`

Todas as rotas exigem `Authorization: Bearer <token>`. Além dos status da tabela, qualquer uma pode responder `401` e `403`.

| Verbo | URI | Descrição | Status |
| :--- | :--- | :--- | :--- |
| `POST` | `/solicitacoes` | Cria uma solicitação de crédito | `201`, `400`, `404`, `409`, `503` |
| `GET` | `/solicitacoes` | Lista todas as solicitações | `200` |
| `GET` | `/solicitacoes/{id}` | Busca uma solicitação pelo id | `200`, `404` |
| `PUT` | `/solicitacoes/{id}/status` | Aprova ou reprova uma solicitação em análise | `204`, `400`, `404`, `409` |
| `GET` | `/clientes/{cpf}/solicitacoes` | Lista as solicitações de um cliente | `200`, `400` |
| `GET` | `/clientes/{cpf}/conta` | Consulta a conta do cliente no Core Bancário | `200`, `400`, `404`, `503` |

Os erros seguem o formato `ProblemDetail`. O `POST /solicitacoes` responde `201` com o cabeçalho `Location` apontando para a solicitação criada, e `404` quando o CPF não tem conta no Core Bancário.

### Tratamento de erros

O `presentation` não enxerga as exceções do `domain`. O `TradutorDeExcecoes`, um aspecto do `application`, intercepta os casos de uso e troca cada exceção do domínio pela exceção da aplicação equivalente, com a mesma mensagem. O `GlobalExceptionHandler` trata as exceções da aplicação e as de segurança (`401` e `403`).

| Exceção da aplicação | Origem | Status |
| :--- | :--- | :--- |
| `DadosInvalidosException` | `CpfInvalidoException`, demais `DominioException` e status em texto desconhecido | `400` |
| `CredenciaisInvalidasException` | login ou senha errados no `AutenticacaoService` | `401` |
| `RecursoNaoEncontradoException` | solicitação inexistente e `ContaNaoEncontradaException` | `404` |
| `ConflitoDeNegocioException` | `TransicaoDeStatusInvalidaException`, `ContaNaoAtivaException` | `409` |
| `ServicoIndisponivelException` | `CoreBancarioIndisponivelException`, com a causa só no log | `503` |

### Criar solicitação

Tratada pelo método `criarProposta` do `SolicitacaoCreditoRestController`. O corpo é um `SolicitarCreditoRequest` e a resposta, um `SolicitacaoResponse`.

```json
{
  "cpfCliente": "12345678900",
  "nomeCliente": "Ana Silva",
  "tipoCredito": "EMPRÉSTIMO PESSOAL",
  "valorSolicitado": 15000.00,
  "quantidadeParcelas": 24,
  "rendaMensal": 7500.00
}
```

### Atualizar status

Tratada pelo método `atualizarStatus` do `SolicitacaoCreditoRestController`. O corpo é um `StatusUpdateRequest`. `status` aceita `APROVADO` ou `REPROVADO`. Só solicitações em `EM_ANALISE` podem mudar de status; nos demais casos a resposta é `409`.

```json
{
  "status": "APROVADO",
  "parecerOperador": "Proposta aprovada conforme política de crédito."
}
```

Quando a mudança é aceita, a resposta é `204` sem corpo, como no trecho do enunciado. Para ver a solicitação atualizada, use `GET /solicitacoes/{id}`. A rota legada `aprovarSolicitacao` continua devolvendo o JSON da solicitação.

---

## 🌐 Adaptador Legado (todas em POST)

Base URL: `http://localhost:8081/api/credito`

A Mesa de Operações e o app mobile usam estas rotas. O `CreditoLegadoController` mantém os mesmos nomes e JSONs e chama o `SolicitacaoCreditoService`. A diferença é que agora toda chamada precisa do `Authorization: Bearer <token>`.

| Rota | Exemplo de Body | Resposta |
| :--- | :--- | :--- |
| `POST /solicitarCredito` | mesmo JSON de criar solicitação | solicitação criada |
| `POST /consultarStatusCredito` | `{ "cpfCliente": "12345678900" }` | lista de solicitações do cliente |
| `POST /buscarSolicitacaoPorId` | `{ "solicitacaoId": 1 }` | solicitação |
| `POST /listarTodasSolicitacoes` | sem body | lista de solicitações |
| `POST /aprovarSolicitacao` | `{ "solicitacaoId": 4, "parecerOperador": "Dentro da política." }` | solicitação atualizada |
| `POST /reprovarSolicitacao` | `{ "solicitacaoId": 4, "motivoReprovacao": "Margem insuficiente." }` | solicitação atualizada |
| `POST /consultarClienteCore` | `{ "cpfCliente": "12345678900" }` | `statusConta`, `saldo`, `agencia`, `numeroConta` |
