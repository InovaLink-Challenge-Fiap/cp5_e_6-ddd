# FIAP Bank - Core Bancário 🏦

Microsserviço de **Core Bancário** responsável pela gestão central de clientes e contas correntes/poupança do FIAP Bank. O projeto segue os princípios de **Domain-Driven Design (DDD)** com segregação estrita em arquitetura multi-módulo Maven.

As dependências seguem a hierarquia do enunciado: `presentation` enxerga `application`; `application` enxerga `infrastructure` e `domain`; `infrastructure` enxerga `domain`; e `domain` não depende de nenhum. O `maven-enforcer-plugin` barra no build qualquer dependência fora dessa ordem. O `presentation` compila só contra o `application`; `domain` e `infrastructure` entram nele só em tempo de execução.

No domínio, o `Cliente` é a raiz do agregado e tem a `ContaBancaria` no atributo `conta`, como no diagrama do enunciado. Por isso só existe o `ClienteRepository`: abrir, movimentar e encerrar a conta passa pelo cliente, e apagar o cliente apaga a conta junto.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1+**
- **Spring Data JPA**
- **Bean Validation**
- **H2 Database** (em memória)
- **Spring Boot Actuator**
- **Lombok** (fora do `domain`)
- **Spring AOP com AspectJ**
- **Maven** (projeto multi-módulo)

---

## 📋 Pré-requisitos

- **Java JDK 21** instalado e configurado nas variáveis de ambiente.
- **Apache Maven 3.9+** instalado.

---

## 🚀 Como Executar a Aplicação

### 1. Compilar os módulos Maven

Na raiz do diretório `fiap-bank-core-bancario`:

```bash
mvn clean install
```

### 2. Iniciar a aplicação

Você pode iniciar o serviço diretamente da raiz do módulo:

```bash
mvn -pl fiap-bank-core-bancario-presentation spring-boot:run
```

Ou navegando até o módulo de apresentação:

```bash
cd fiap-bank-core-bancario-presentation
mvn spring-boot:run
```

### 3. Executar via arquivo JAR

Após o empacotamento com `mvn clean package`:

```bash
java -jar fiap-bank-core-bancario-presentation/target/fiap-bank-core-bancario-presentation-1.0.0-SNAPSHOT.jar
```

A aplicação estará acessível em: **`http://localhost:8080`**

---

## 🗄️ Console do Banco de Dados H2

O banco de dados H2 em memória pode ser acessado pelo navegador:

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:corebancariodb`
- **User Name:** `sa`
- **Password:** *(deixe em branco)*

---

## 📌 Dados Iniciais (Carga Automática)

Ao subir o serviço, os seguintes dados são carregados automaticamente via `InicializadorDeDados`:

- **Cliente 1:** Ana Silva | CPF: `12345678900` | Agência: `0001` | Conta: `12345-6` | Saldo: `R$ 12.500,00`
- **Cliente 2:** Carlos Eduardo Oliveira | CPF: `98765432100` | Agência: `0001` | Conta: `78901-2` | Saldo: `R$ 45.800,50`
- **Cliente 3:** Mariana Souza | CPF: `55544433399` | Agência: `0001` | Conta: `33333-3` | Saldo: `R$ 800,00`
- **Cliente 4:** Bruno Ferreira | CPF: `11122233347` | Agência: `0001` | Conta: `44444-7` | Saldo: `R$ 3.200,00`

---

## 🌐 Endpoints RESTful Principais

### Clientes (`/api/v1/clientes`)

| Método | Endpoint | Descrição | Status |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/clientes` | Lista todos os clientes cadastrados | `200` |
| `GET` | `/api/v1/clientes/{id}` | Busca cliente por UUID | `200`, `400`, `404` |
| `GET` | `/api/v1/clientes/cpf/{cpf}` | Busca cliente por CPF | `200`, `400`, `404` |
| `POST` | `/api/v1/clientes` | Cadastra um novo cliente (`Location` no retorno) | `201`, `400`, `409` |
| `PUT` | `/api/v1/clientes/{id}` | Atualiza integralmente os dados do cliente (mesmo corpo do `POST`) | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/v1/clientes/{id}` | Apaga o cliente e a conta dele | `204`, `400`, `404` |

### Contas Bancárias (`/api/v1/contas`)

| Método | Endpoint | Descrição | Status |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/contas` | Lista todas as contas bancárias | `200` |
| `GET` | `/api/v1/contas/{numeroConta}` | Busca detalhes de uma conta por número | `200`, `400`, `404` |
| `GET` | `/api/v1/contas/cliente/{cpf}` | Busca conta bancária associada a um CPF | `200`, `400`, `404` |
| `POST` | `/api/v1/contas` | Abre uma nova conta bancária para um cliente já cadastrado (`Location` no retorno) | `201`, `400`, `409` |
| `PUT` | `/api/v1/contas/{numeroConta}/saldo` | Atualiza o saldo da conta (`DEPOSITO` ou `SAQUE`) | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/v1/contas/{numeroConta}` | Encerra a conta (status `ENCERRADA`, sem apagar o registro) | `204`, `400`, `404` |

Os erros seguem o formato `ProblemDetail` (`application/problem+json`). Erros de validação trazem a lista de campos em `erros`. Número de conta fora do formato `NNNNN-D` responde `400`.

### Corpo das requisições

Os DTOs são `record`s do pacote `br.com.fiap.bank.core.application.dto`, validados com Bean Validation.

| DTO | Rotas | Validações |
| :--- | :--- | :--- |
| `CriarClienteRequest` | `POST /api/v1/clientes` e `PUT /api/v1/clientes/{id}` | `cpf` com 11 dígitos ou no formato `XXX.XXX.XXX-XX`; `nome` de 3 a 150 caracteres; `email` válido; `dataNascimento` no passado; `rendaMensal` maior ou igual a zero |
| `CriarContaRequest` | `POST /api/v1/contas` | `cpfCliente` obrigatório; `agencia` opcional (sem ela, `0001`), mas com 4 dígitos se vier; `tipoConta` `CORRENTE`, `POUPANCA` ou `SALARIO`; `saldoInicial` maior ou igual a zero |
| `AtualizarSaldoRequest` | `PUT /api/v1/contas/{numeroConta}/saldo` | `operacao` `DEPOSITO` ou `SAQUE`, em maiúscula ou minúscula; `valor` maior que zero |

```json
{
  "cpf": "111.444.777-35",
  "nome": "João Pereira",
  "email": "joao.pereira@email.com",
  "dataNascimento": "1990-05-20",
  "rendaMensal": 6500.00
}
```

```json
{
  "cpfCliente": "11144477735",
  "agencia": "0001",
  "tipoConta": "CORRENTE",
  "saldoInicial": 1000.00
}
```

```json
{
  "operacao": "DEPOSITO",
  "valor": 250.00
}
```

Exemplo de retorno de uma conta:

```json
{
  "id": "91fc53ac-7816-45ec-89bc-0fd7bad2c429",
  "numeroConta": "12345-6",
  "agencia": "0001",
  "tipoConta": "CORRENTE",
  "saldo": 12500.00,
  "statusConta": "ATIVA",
  "dataAbertura": "2026-11-03",
  "cpfCliente": "12345678900",
  "nomeCliente": "Ana Silva"
}
```
