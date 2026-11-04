# FIAP Bank - Mesa de Operações de Crédito 🖥️

Front-end Web de **Mesa de Operações de Crédito** do FIAP Bank. Permite que operadores bancários e analistas de crédito monitorem o painel de propostas, analisem detalhadamente o perfil financeiro de clientes (score, restrições no SCR, dados da conta bancária) e tomem decisões de aprovação ou reprovação de crédito com registro de parecer técnico.

A comunicação com a **Plataforma de Crédito** é realizada de forma declarativa utilizando o **Spring Cloud OpenFeign** (`@FeignClient`).

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1**
- **Spring Cloud 2025.1.4** com **OpenFeign**
- **Thymeleaf** (renderização server-side de templates HTML)
- **Spring Web**
- **Maven**

---

## 📋 Pré-requisitos

- **Java JDK 21** instalado e configurado no `PATH`.
- **Apache Maven 3.9+** instalado.
- O serviço **Plataforma de Crédito** deve estar em execução na porta `8081` para carregar e gerenciar as solicitações reais.

---

## 🚀 Como Executar a Aplicação

### 1. Compilar o projeto

Na raiz do diretório `fiap-bank-mesa-operacoes-credito`:

```bash
mvn clean compile
```

### 2. Iniciar a aplicação via Maven

```bash
mvn spring-boot:run
```

### 3. Executar via arquivo JAR

Para gerar o pacote e executar:

```bash
mvn clean package -DskipTests
java -jar target/fiap-bank-mesa-operacoes-credito-1.0.0-SNAPSHOT.jar
```

A aplicação web estará acessível no navegador em: **`http://localhost:8082`**

---

## ⚙️ Variáveis de Ambiente e Configuração

As seguintes propriedades podem ser customizadas no arquivo `src/main/resources/application.yml` ou passadas via variáveis de ambiente:

| Propriedade | Variável de Ambiente | Valor Padrão | Descrição |
| :--- | :--- | :--- | :--- |
| `server.port` | `SERVER_PORT` | `8082` | Porta HTTP da aplicação |
| `plataforma.credito.url` | `PLATAFORMA_CREDITO_URL` | `http://localhost:8081` | URL base da Plataforma de Crédito consumida via Feign |

---

## 🖥️ Páginas e Funcionalidades

### 1. Dashboard de Operações (`/` ou `/dashboard`)
- **Cards de Métricas:** Total de solicitações, propostas em análise, aprovadas e reprovadas.
- **Tabela de Solicitações:** Listagem completa com ID, Nome, CPF, Tipo de Crédito, Valor Solicitado, Score, Status e botão de ação para análise.

### 2. Análise Detalhada da Proposta (`/solicitacao/{id}`)
- Exibe o resumo cadastral do cliente e condições do crédito (parcelas, renda informada).
- Apresenta o resultado do motor de risco: Score de Crédito do Bureau, restrições no SCR (Banco Central).
- Formulário para registrar o parecer do operador bancário e botões para **Aprovar** ou **Reprovar** a proposta.

### 3. Rotas da Mesa

| Verbo | Rota | O que faz |
| :--- | :--- | :--- |
| `GET` | `/login` | Tela de login |
| `POST` | `/login` | Autentica na Plataforma e grava o cookie `access_token` |
| `GET` | `/logout` | Apaga o cookie e volta para o login |
| `GET` | `/` ou `/dashboard` | Painel de solicitações |
| `GET` | `/solicitacao/{id}` | Análise detalhada da proposta |
| `POST` | `/aprovar` | Aprova a proposta (`solicitacaoId` e `parecerOperador`) |
| `POST` | `/reprovar` | Reprova a proposta (`solicitacaoId` e `motivoReprovacao`) |

---

## ⚙️ Login e Segurança

A Plataforma de Crédito é um **OAuth2 Resource Server**: as rotas usadas pela Mesa exigem um JWT de um usuário com perfil `OPERADOR` ou `ADMIN`. Por isso, toda página da Mesa, exceto `/login`, `/logout`, `/error` e os arquivos estáticos, exige login.

### Usuários de teste

| Usuário | Senha | Perfil | Acessa a Mesa |
| :--- | :--- | :--- | :--- |
| `operador` | `operador123` | `OPERADOR` | Sim |
| `admin` | `admin123` | `ADMIN` | Sim |
| `12345678900` | `123456` | `CLIENTE` | Não |

### Como o token flui

1. O formulário de `/login` envia usuário e senha para a Mesa, que chama `POST /api/auth/login` na Plataforma via Feign (`AutenticacaoFeignClient`).
2. Em caso de sucesso, a Mesa grava o JWT no cookie `access_token` (`HttpOnly`, `Path=/`, `SameSite=Strict`, `Max-Age` igual ao `expiresIn`).
3. O `AutenticacaoInterceptor` manda para `/login` qualquer requisição que chegue sem esse cookie e sem header `Authorization: Bearer`.
4. O `RequestInterceptor` definido em `ConfiguracaoFeign` lê o header `Authorization` da requisição atual ou, na falta dele, o cookie, e envia `Authorization: Bearer <token>` em toda chamada Feign. A chamada de login não leva token.

### Expiração e permissões

| Situação | Resposta da Plataforma | O que a Mesa faz |
| :--- | :--- | :--- |
| Usuário ou senha inválidos | `400` ou `401` no login | Volta para `/login?erro=credenciais` |
| Plataforma fora do ar | Nenhuma resposta ou outro erro no login | Volta para `/login?erro=indisponivel` |
| Usuário sem perfil `OPERADOR` ou `ADMIN` | `200` no login | Volta para `/login?erro=perfil`, sem gravar o cookie |
| Token expirado ou inválido | `401` | Apaga o cookie e volta para `/login?erro=expirado` |
| Sem permissão para a operação | `403` | Mostra o aviso de permissão no dashboard |
| Solicitação já aprovada ou reprovada | `409` | Volta ao dashboard com a mensagem de que a solicitação já foi decidida |

O token vale **2 horas** e não é renovado: depois disso é preciso entrar de novo. Para sair, acesse `/logout`.
