# FIAP Bank - Esteira de Crédito 🏦💳

Monorepo contendo o ecossistema completo da **Esteira de Crédito** do FIAP Bank, composto por quatro aplicações integradas: Core Bancário, Plataforma de Crédito, Mesa de Operações e Internet Banking Mobile.

Trabalho da disciplina **Domain Driven Design - Java** (FIAP, Engenharia de Software, turma 2ESPH), Checkpoint 6: **FIAP Bank - Plataforma de Concessão de Crédito e Segurança**, feito sobre a base do Checkpoint 5.

| Aluno | RM |
| :--- | :--- |
| Kauê de Almeida Pena | 564211 |
| Eduardo Delorenzo Moraes | 561749 |
| Ronaldo Aparecido Monteiro Almeida | 565017 |
| William Queiroz | 565032 |

---

## 📌 O que foi feito

Checkpoint 5: o Core Bancário foi feito do zero em 4 módulos Maven e a Plataforma de Crédito foi refatorada para DDD, também em 4 módulos, com API RESTful em `/api/v1` e as rotas antigas `/api/credito/*` mantidas para a Mesa e o app.

Checkpoint 6: a Plataforma virou um OAuth2 Resource Server com JWT. O app mobile e a Mesa ganharam tela de login e mandam o token em toda chamada. As telas que já existiam mantêm o visual, a navegação entre elas e as regras; o que entrou foi a tela de login antes delas, o botão Sair no app e os ajustes de espaçamento e de texto descritos no README do app.

O README de cada projeto tem os detalhes.

---

## 📁 Estrutura do Workspace e Documentação

Cada projeto possui seu próprio arquivo `README.md` detalhando tecnologias, arquitetura, rotas e instruções de execução:

| Projeto | Tecnologia | Porta | Descrição & Documentação |
| :--- | :--- | :---: | :--- |
| **`fiap-bank-core-bancario`** | Java 21, Spring Boot 4.1, Spring Data JPA, H2, DDD Multi-Módulo | `8080` | [README do Core Bancário](./fiap-bank-core-bancario/README.md) |
| **`fiap-bank-plataforma-credito`** | Java 21, Spring Boot 4.1, Spring Security (OAuth2 Resource Server, JWT), Spring Cloud OpenFeign, DDD Multi-Módulo | `8081` | [README da Plataforma de Crédito](./fiap-bank-plataforma-credito/README.md) |
| **`fiap-bank-mesa-operacoes-credito`** | Java 21, Spring Boot 4.1, Thymeleaf, Spring Cloud OpenFeign | `8082` | [README da Mesa de Operações](./fiap-bank-mesa-operacoes-credito/README.md) |
| **`fiap-bank-internet-banking-mobile`** | Angular 17, TypeScript, CSS Responsivo | `4200` | [README do Mobile Angular](./fiap-bank-internet-banking-mobile/README.md) |

---

## 📋 Pré-requisitos

- **Java JDK 21**
- **Apache Maven 3.9+**
- **Node.js 18 ou 20** e **npm** (para o app mobile)

---

## 🚦 Ordem Recomendada de Execução

Para testar o fluxo de ponta a ponta com todas as integrações ativas, inicie os serviços na seguinte ordem:

### 1. Iniciar o Core Bancário (`Porta 8080`)
Responsável pela gestão de clientes e contas bancárias.
```bash
cd fiap-bank-core-bancario
mvn clean install
mvn -pl fiap-bank-core-bancario-presentation spring-boot:run
```
- API Base: `http://localhost:8080/api/v1`
- H2 Console: `http://localhost:8080/h2-console`

---

### 2. Iniciar a Plataforma de Crédito (`Porta 8081`)
Motor de decisão e esteira, integrado via Spring Cloud OpenFeign ao Core Bancário. Precisa do Core no ar para carregar as solicitações iniciais.
```bash
cd fiap-bank-plataforma-credito
mvn clean install
mvn -pl fiap-bank-plataforma-credito-presentation spring-boot:run
```
- Login: `POST http://localhost:8081/api/auth/login`
- API RESTful: `http://localhost:8081/api/v1`
- Adaptador legado: `http://localhost:8081/api/credito`
- H2 Console: `http://localhost:8081/h2-console`

---

### 3. Iniciar a Mesa de Operações (`Porta 8082`)
Front-end Web Thymeleaf para analistas de risco aprovarem ou reprovarem propostas.
```bash
cd fiap-bank-mesa-operacoes-credito
mvn clean compile
mvn spring-boot:run
```
- Painel Web do Operador: `http://localhost:8082` (entre com `operador` / `operador123`)

---

### 4. Iniciar o Internet Banking Mobile (`Porta 4200`)
Front-end Angular responsivo voltado ao cliente final para simulação e submissão de crédito.
```bash
cd fiap-bank-internet-banking-mobile
npm install
npm run start
```
- App Mobile Web: `http://localhost:4200` (entre com o CPF `12345678900` e a senha `123456`)

---

## 📋 Usuários de Teste

| Login | Senha | Perfil | Onde usar |
| :--- | :--- | :--- | :--- |
| `12345678900` | `123456` | `CLIENTE` (Ana Silva) | App mobile |
| `98765432100` | `123456` | `CLIENTE` (Carlos Eduardo Oliveira) | App mobile |
| `55544433399` | `123456` | `CLIENTE` (Mariana Souza) | App mobile |
| `11122233347` | `123456` | `CLIENTE` (Bruno Ferreira) | App mobile |
| `operador` | `operador123` | `OPERADOR` | Mesa de Operações |
| `admin` | `admin123` | `ADMIN` | Mesa de Operações |

Exemplo de login direto na API:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"12345678900","senha":"123456"}'
```

O `accessToken` da resposta vai no cabeçalho `Authorization: Bearer <token>` das demais chamadas. O token vale 2 horas.

---

## 🧪 Dados Pré-Carregados para Testes

Os serviços iniciam automaticamente com dados pré-populados para testes:

- **Cliente 1 (Aprovável):** CPF `12345678900` | Nome: Ana Silva | Renda: R$ 7.500,00 | Saldo: R$ 12.500,00
- **Cliente 2 (Aprovável):** CPF `98765432100` | Nome: Carlos Eduardo Oliveira | Renda: R$ 12.000,00 | Saldo: R$ 45.800,50
- **Cliente 3 (Reprovado SCR):** CPF `55544433399` | Nome: Mariana Souza | Renda: R$ 4.200,00 | Saldo: R$ 800,00 | Restrição ativa no SCR
- **Cliente 4 (Inadimplente no CRM):** CPF `11122233347` | Nome: Bruno Ferreira | Renda: R$ 5.000,00 | Saldo: R$ 3.200,00 | Inadimplência no histórico do CRM

A Plataforma sobe com cinco solicitações: duas aprovadas automaticamente, uma reprovada pelo SCR e duas **em análise**, prontas para serem decididas na Mesa de Operações. A da Ana Silva (R$ 80.000,00 em 12 parcelas) foi para a Mesa porque a parcela passa de 30% da renda; a do Bruno Ferreira (R$ 6.000,00 em 12 parcelas), pela inadimplência no CRM.
