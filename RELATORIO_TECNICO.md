# e-Restaurante — Sistema de Gestão para Restaurantes
## Relatório Técnico — Fase 1: Domínio de Usuário

**Autores:**
- [Nome completo do Aluno 1]
- [Nome completo do Aluno 2]
- [Nome completo do Aluno 3] (se aplicável)
- [Nome completo do Aluno 4] (se aplicável)

**Equipe:** [Nome da Equipe]  
**Data:** 25/08/2026  
**Instituição:** FIAP — Faculdade de Informática e Administração Paulista

---

## 1. Introdução

Este documento descreve as atividades, decisões arquiteturais e implementação realizadas pela equipe na Fase 1 do Tech Challenge de Arquitetura e Desenvolvimento Java. O projeto, denominado **e-Restaurante**, visa construir um sistema de gestão compartilhado para um grupo de restaurantes da região que optou por contratar uma solução única em vez de cada estabelecimento manter sua própria plataforma.

A estratégia de produto está dividida em fases de entrega. Esta Fase 1 estabelece a fundação do sistema com foco exclusivo no **domínio de Usuário**: cadastro, autenticação e gerenciamento de perfis. As funcionalidades de Restaurante, Cardápio e Pedido serão introduzidas em fases subsequentes.

A solução foi construída com **Java 25**, **Spring Boot 4.1.1** e **arquitetura hexagonal (Ports & Adapters)**, com atenção explícita aos princípios SOLID e às práticas de Domain-Driven Design (DDD). O entregável principal desta fase é este relatório em PDF, acompanhado do código-fonte disponível no repositório GitHub referenciado na seção 8.

---

## 2. Definição do Problema

Um grupo de restaurantes de pequeno e médio porte enfrenta o desafio comum de gerenciar seus sistemas de forma isolada e custosa. Cada estabelecimento mantém soluções próprias — frequentemente planilhas, sistemas legados ou aplicativos genéricos — que não se comunicam entre si e não atendem às necessidades específicas do negócio de alimentação.

**Causas identificadas:**
- Alto custo individual de desenvolvimento e manutenção de software proprietário
- Ausência de uma plataforma unificada que permita aos clientes descobrir e interagir com múltiplos restaurantes
- Falta de rastreabilidade de pedidos, avaliações e histórico de clientes
- Dificuldade de escalar operações sem uma base tecnológica sólida

**Impactos:**
- Perda de clientes para plataformas de terceiros (iFood, Uber Eats) que cobram altas taxas de comissão
- Ausência de dados próprios sobre comportamento e preferências dos clientes
- Ineficiência operacional por falta de integração entre cadastro, pedido e entrega

**Relevância:**
A criação de um sistema próprio, compartilhado entre os restaurantes participantes, reduz o custo per capita de desenvolvimento, mantém os dados sob controle do consórcio e abre espaço para funcionalidades customizadas que plataformas genéricas não oferecem.

---

## 3. Levantamento de Requisitos

### 3.1 Event Storming

O Event Storming é uma técnica de modelagem colaborativa criada por Alberto Brandolini que permite mapear o fluxo de eventos de um domínio de negócio de forma visual e inclusiva, envolvendo tanto desenvolvedores quanto especialistas de negócio.

Para o domínio de Usuário desta fase, foram identificados os seguintes elementos:

**Eventos de Domínio** (fatos que aconteceram no passado):
- `UsuarioCadastrado` — um novo usuário foi registrado no sistema
- `DadosDoUsuarioAtualizados` — nome, email ou endereço foram alterados
- `SenhaDoUsuarioAlterada` — a senha foi trocada com validação da senha atual
- `UsuarioAutenticado` — login realizado com sucesso
- `AutenticacaoRecusada` — credenciais inválidas fornecidas
- `UsuarioExcluido` — registro removido do sistema

**Comandos** (intenções que disparam eventos):
- `CadastrarUsuario` → dispara `UsuarioCadastrado`
- `AtualizarUsuario` → dispara `DadosDoUsuarioAtualizados`
- `TrocarSenha` → dispara `SenhaDoUsuarioAlterada`
- `RealizarLogin` → dispara `UsuarioAutenticado` ou `AutenticacaoRecusada`
- `ExcluirUsuario` → dispara `UsuarioExcluido`

**Agregados identificados:**
- `Usuario` — raiz do agregado, com `Endereco` como objeto de valor embutido

**Políticas de negócio (regras):**
- Email deve ser único em todo o sistema (validado na camada de serviço, não apenas no banco)
- Troca de senha exige confirmação da senha atual
- O campo `dataUltimaAlteracao` deve ser atualizado automaticamente em toda escrita

**Fluxo principal mapeado:**

```
[Ator: Novo Usuário]
     │
     ▼
[Comando: CadastrarUsuario]
     │  valida email único
     │  criptografa senha (BCrypt)
     ▼
[Evento: UsuarioCadastrado]
     │
     ▼
[Ator: Usuário Cadastrado]
     │
     ▼
[Comando: RealizarLogin]
     │  verifica login + senha hash
     ▼
[Evento: UsuarioAutenticado]
```

### 3.2 Mapeamento de Demandas e Necessidades

**Perfil 1 — Cliente:**
- Necessidade de se cadastrar na plataforma para realizar pedidos futuros (Fase 2)
- Manter seus dados pessoais e endereço de entrega atualizados
- Autenticar-se com segurança para acessar seu histórico e preferências

**Perfil 2 — Dono de Restaurante:**
- Necessidade de se registrar como responsável por um ou mais estabelecimentos
- Gerenciar seu acesso à plataforma de administração
- Nas fases seguintes: vincular-se a restaurantes e gerenciar cardápio, horários e pedidos

**Partes interessadas (stakeholders):**
- **Consórcio de restaurantes** — patrocinam o sistema e definem as regras de negócio
- **Clientes finais** — usuários que farão pedidos e avaliações
- **Equipe de desenvolvimento** — responsável pela implementação e evolução do sistema
- **FIAP** — avaliação acadêmica do projeto

### 3.3 Identificação dos Agregados (Domínios)

**Nesta Fase 1, um único agregado foi implementado:**

| Agregado | Entidade Raiz | Objetos de Valor | Responsabilidade |
|---|---|---|---|
| **Usuário** | `Usuario` (abstrato) | `Endereco` | Identidade, autenticação e dados pessoais dos participantes do sistema |

**Subtipos do agregado Usuário:**

| Subtipo | Discriminador | Papel no sistema |
|---|---|---|
| `Cliente` | `CLIENTE` | Perfil para clientes que fazem pedidos |
| `DonoRestaurante` | `DONO_RESTAURANTE` | Perfil para responsáveis pelos estabelecimentos |

**Limites do agregado Usuário (Bounded Context):**
- Responsável por: cadastro, autenticação, atualização de dados e exclusão
- Não responsável por: pedidos, avaliações, restaurantes, cardápio — escopo de fases futuras

**Diagrama de relacionamento conceitual:**

```
┌─────────────────────────────────────────┐
│              Bounded Context             │
│               USUARIO                   │
│                                         │
│   ┌─────────────┐                       │
│   │   Usuario   │ ◄── raiz do agregado  │
│   │  (abstrato) │                       │
│   └──────┬──────┘                       │
│          │ herança                      │
│    ┌─────┴──────┐                       │
│    │            │                       │
│ ┌──▼──┐   ┌────▼──────────┐            │
│ │     │   │               │            │
│ │Cli- │   │ DonoRestau-   │            │
│ │ente │   │ rante         │            │
│ └─────┘   └───────────────┘            │
│                                         │
│   ┌──────────┐                          │
│   │ Endereco │ ◄── value object         │
│   └──────────┘                          │
└─────────────────────────────────────────┘
```

---

## 4. Arquitetura do Sistema

### 4.1 Abordagem Domain-Driven Design (DDD)

O projeto adota DDD como filosofia de modelagem, com ênfase na separação entre a lógica de negócio (domínio) e os detalhes técnicos (infraestrutura). A premissa central é que o código deve refletir a linguagem do negócio — o que DDD chama de *Ubiquitous Language*.

**Como o DDD foi aplicado:**

- **Linguagem ubíqua:** os nomes de classes, métodos e variáveis espelham o vocabulário do negócio: `Usuario`, `Endereco`, `CadastrarUsuarioUseCase`, `TrocarSenha`. Um especialista de negócio consegue ler o código de domínio sem conhecer Java.
- **Agregados com fronteiras claras:** `Usuario` é a única raiz de agregado desta fase. Nenhum objeto externo modifica `Endereco` diretamente — apenas através de `Usuario`.
- **Bounded Context:** o código do domínio de Usuário está isolado em seu próprio pacote (`domain/`), sem conhecimento de pedidos, restaurantes ou qualquer contexto futuro.
- **Repositórios como abstração:** o domínio define `UsuarioRepositoryPort` (interface); a infraestrutura fornece a implementação JPA. O domínio não sabe que existe um banco PostgreSQL.

**Benefícios para este projeto:**
- Facilidade de evolução: adicionar o domínio Restaurante na Fase 2 sem modificar o domínio de Usuário
- Testabilidade: a lógica de negócio pode ser testada sem banco de dados ou servidor HTTP
- Alinhamento com o negócio: decisões técnicas são justificadas por necessidades do domínio

### 4.2 Definição dos Domínios

**Estrutura de pacotes implementada:**

```
br.com.fiap.erestaurante/
│
├── domain/                          ← núcleo — zero dependência de framework
│   ├── model/                       ← entidades e value objects
│   │   ├── Usuario.java             ← entidade raiz (abstrata)
│   │   ├── Cliente.java             ← subtipo concreto
│   │   ├── DonoRestaurante.java     ← subtipo concreto
│   │   ├── Endereco.java            ← value object imutável
│   │   └── TipoUsuario.java         ← enum discriminador
│   ├── port/
│   │   ├── in/                      ← portas de entrada (use cases)
│   │   │   ├── CadastrarUsuarioUseCase.java
│   │   │   ├── AtualizarUsuarioUseCase.java
│   │   │   ├── ExcluirUsuarioUseCase.java
│   │   │   ├── BuscarUsuarioUseCase.java
│   │   │   ├── TrocarSenhaUseCase.java
│   │   │   └── AutenticarUsuarioUseCase.java
│   │   └── out/                     ← portas de saída (repositórios)
│   │       └── UsuarioRepositoryPort.java
│   └── exception/                   ← hierarquia de exceções de domínio
│       ├── DomainException.java
│       ├── NotFoundException.java   → HTTP 404
│       ├── ConflictException.java   → HTTP 409
│       ├── BusinessException.java   → HTTP 400
│       ├── UsuarioNaoEncontradoException.java
│       ├── EmailJaCadastradoException.java
│       └── SenhaInvalidaException.java
│
├── application/                     ← casos de uso — apenas Java puro
│   └── usecase/
│       ├── CadastrarUsuarioUseCaseImpl.java
│       ├── AtualizarUsuarioUseCaseImpl.java
│       ├── ExcluirUsuarioUseCaseImpl.java
│       ├── BuscarUsuarioUseCaseImpl.java
│       ├── TrocarSenhaUseCaseImpl.java
│       └── AutenticarUsuarioUseCaseImpl.java
│
├── infrastructure/                  ← adaptadores — depende de frameworks
│   ├── persistence/
│   │   ├── entity/                  ← entidades JPA
│   │   │   ├── UsuarioEntity.java
│   │   │   ├── ClienteEntity.java
│   │   │   ├── DonoRestauranteEntity.java
│   │   │   └── EnderecoEntity.java  ← @Embeddable
│   │   ├── mapper/
│   │   │   └── UsuarioPersistenceMapper.java  ← MapStruct
│   │   └── repository/
│   │       ├── UsuarioRepository.java         ← Spring Data JPA
│   │       └── UsuarioRepositoryAdapter.java  ← implementa UsuarioRepositoryPort
│   └── web/
│       ├── controller/
│       │   ├── UsuarioController.java
│       │   └── LoginController.java
│       ├── dto/
│       │   ├── request/             ← DTOs de entrada com Bean Validation
│       │   └── response/            ← DTOs de saída
│       ├── mapper/
│       │   └── UsuarioWebMapper.java  ← MapStruct
│       └── exception/
│           └── GlobalExceptionHandler.java  ← ProblemDetail RFC 7807
│
└── config/                          ← configurações Spring
    ├── BeanConfiguration.java       ← wiring dos use cases sem @Service
    ├── SecurityConfig.java          ← desabilita HTTP Security (fase futura)
    └── OpenApiConfig.java           ← metadados do Swagger
```

**Fluxo de dependências (Dependency Rule da Arquitetura Hexagonal):**

```
infrastructure  ──depende de──►  application  ──depende de──►  domain
                                                               (núcleo puro)
```

A seta de dependência NUNCA aponta para fora do núcleo. O domínio não conhece Spring, JPA, HTTP ou qualquer detalhe de infraestrutura.

### 4.3 Modelagem Conceitual

**Entidades e Objetos de Valor:**

| Elemento | Tipo DDD | Descrição |
|---|---|---|
| `Usuario` | Entidade (raiz de agregado) | Identidade única por `id`, possui comportamento próprio |
| `Cliente` | Entidade (especialização) | Subtipo com papel de cliente da plataforma |
| `DonoRestaurante` | Entidade (especialização) | Subtipo com papel de responsável por restaurante |
| `Endereco` | Value Object | Imutável, sem identidade própria, definido pelos seus atributos |
| `TipoUsuario` | Enum | Discriminador de tipo em tempo de execução |

**Atributos da entidade Usuario:**

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | `Long` | Gerado pelo banco (BIGINT IDENTITY) |
| `nome` | `String` | Obrigatório, máx. 150 caracteres |
| `email` | `String` | Obrigatório, único no sistema, formato válido |
| `login` | `String` | Obrigatório, único no sistema |
| `senha` | `String` | Obrigatório, armazenada como hash BCrypt (strength 12) |
| `endereco` | `Endereco` | Obrigatório, value object embutido |
| `dataUltimaAlteracao` | `LocalDateTime` | Atualizado automaticamente via `@PrePersist`/`@PreUpdate` |

**Atributos do Value Object Endereco:**

| Campo | Tipo | Restrição |
|---|---|---|
| `logradouro` | `String` | Obrigatório, máx. 200 caracteres |
| `numero` | `String` | Obrigatório, máx. 20 caracteres |
| `complemento` | `String` | Opcional, máx. 100 caracteres |
| `bairro` | `String` | Obrigatório, máx. 100 caracteres |
| `cidade` | `String` | Obrigatório, máx. 100 caracteres |
| `estado` | `String` | Obrigatório, 2 caracteres (sigla UF) |
| `cep` | `String` | Obrigatório, formato `XXXXX-XXX` |

**Portas de Entrada (Use Cases) — contratos do domínio:**

| Interface | Método | Entrada | Saída |
|---|---|---|---|
| `CadastrarUsuarioUseCase` | `execute(Command)` | dados do usuário + tipo | `Usuario` |
| `AtualizarUsuarioUseCase` | `execute(Long, Command)` | id + dados atualizados | `Usuario` |
| `ExcluirUsuarioUseCase` | `execute(Long)` | id | `void` |
| `BuscarUsuarioUseCase` | `buscarPorId(Long)` | id | `Usuario` |
| `BuscarUsuarioUseCase` | `buscarPorNome(String)` | nome (parcial) | `List<Usuario>` |
| `TrocarSenhaUseCase` | `execute(Long, Command)` | id + senhas | `void` |
| `AutenticarUsuarioUseCase` | `execute(Command)` | login + senha | `Usuario` |

**Porta de Saída (Repositório) — contrato de persistência:**

| Método | Descrição |
|---|---|
| `salvar(Usuario)` | Persiste ou atualiza |
| `buscarPorId(Long)` | Busca por chave primária |
| `buscarPorEmail(String)` | Busca por email para validação de unicidade |
| `buscarPorLogin(String)` | Busca por login para autenticação |
| `buscarPorNome(String)` | Busca parcial, case-insensitive |
| `existePorEmail(String)` | Verificação de unicidade sem carregar entidade |
| `excluir(Long)` | Remove o registro |

---

## 5. Documentação do Projeto

### 5.1 Decisões Arquiteturais

#### DA-01: Arquitetura Hexagonal (Ports & Adapters)

**Decisão:** Adotar arquitetura hexagonal como estilo arquitetural principal.

**Justificativa:** O domínio de negócio (Usuário) deve ser completamente isolado de detalhes tecnológicos. Isso garante que a troca de banco de dados, framework web ou mecanismo de filas não afete a lógica de negócio. A arquitetura hexagonal também facilita testes unitários da lógica de negócio sem necessidade de infraestrutura.

**Consequência:** Três camadas bem definidas — `domain` (puro Java), `application` (casos de uso), `infrastructure` (Spring, JPA, HTTP).

#### DA-02: Herança com Estratégia SINGLE_TABLE

**Decisão:** Usar herança JPA com `InheritanceType.SINGLE_TABLE` para `Cliente` e `DonoRestaurante`.

**Justificativa:** Nesta fase, os dois perfis não têm campos divergentes. `SINGLE_TABLE` elimina JOINs, simplifica queries e é a estratégia mais performática para consultas polimórficas. Um campo discriminador `dtype` (`CLIENTE` | `DONO_RESTAURANTE`) diferencia os tipos em runtime.

**Consequência:** Caso os perfis acumulem muitos campos exclusivos nas fases futuras, a migração para `JOINED` exigirá apenas mudanças na camada de infraestrutura, sem tocar no domínio.

#### DA-03: Use Cases como @Bean (sem @Service na camada application)

**Decisão:** Os use cases são instanciados explicitamente em `BeanConfiguration` em vez de usar `@Service`.

**Justificativa:** Manter a camada `application` livre de anotações Spring garante que ela seja Java puro — testável sem contexto Spring, portável para outros frameworks (Quarkus, Micronaut) e fiel ao Princípio da Inversão de Dependência. A `BeanConfiguration` serve como o ponto único de composição, tornando explícito o grafo de dependências.

#### DA-04: Hierarquia de Exceções por Código HTTP

**Decisão:** Criar exceções de domínio organizadas em uma hierarquia: `DomainException → NotFoundException (404) / ConflictException (409) / BusinessException (400)`.

**Justificativa:** O `GlobalExceptionHandler` captura as classes BASE da hierarquia. Ao criar uma nova exceção de domínio (ex: `RestauranteNaoEncontradoException`), ela herda de `NotFoundException` e automaticamente recebe tratamento HTTP 404 sem modificar o handler — aplicando o Princípio Aberto/Fechado (OCP).

#### DA-05: Liquibase para Controle de Schema

**Decisão:** Usar Liquibase com changelogs em YAML em vez de `ddl-auto: create`.

**Justificativa:** O controle de schema por versão garante reprodutibilidade entre ambientes (local, CI, produção). Cada changeset é rastreável, revisável via pull request e reversível com `rollback`. O JPA está configurado com `ddl-auto: validate` para confirmar que o schema está correto sem tentar modificá-lo.

#### DA-06: ProblemDetail (RFC 7807) para Erros

**Decisão:** Todas as respostas de erro seguem o padrão RFC 7807 via `ProblemDetail` nativo do Spring 6.

**Justificativa:** Respostas de erro com formato consistente (`type`, `title`, `status`, `detail`) permitem que clientes da API (frontend, Postman, outros serviços) tratem erros de forma uniforme. O Spring Boot 4 oferece suporte nativo, sem dependências extras.

### 5.2 Stack Tecnológica

| Componente | Tecnologia | Versão | Justificativa |
|---|---|---|---|
| Linguagem | Java | 25 (LTS) | Versão LTS mais recente, alinhada com Spring Boot 4 |
| Framework | Spring Boot | 4.1.1 | Versão mais recente, construída com Java 25 |
| Build | Gradle | 9.7.1 | Suporte a Java 25; Kotlin DSL para type-safety |
| Banco de dados | PostgreSQL | 16 | Banco relacional robusto e open-source |
| Migrations | Liquibase | (BOM) | Controle de versão de schema com suporte a rollback |
| ORM | Spring Data JPA / Hibernate | (BOM) | Abstração de persistência padrão do ecossistema Spring |
| Mapeamento | MapStruct | 1.6.3 | Geração de código compile-time, sem reflection em runtime |
| Boilerplate | Lombok | (BOM) | Redução de código repetitivo em entidades e DTOs |
| Documentação | SpringDoc OpenAPI | 2.8.9 | Swagger UI integrado para documentação e teste da API |
| Containerização | Docker + Docker Compose | — | Ambiente reproduzível para desenvolvimento e produção |

### 5.3 Endpoints da API

**Base URL:** `http://localhost:8080`  
**Versionamento:** prefixo `/v1/` em todas as rotas  
**Documentação interativa:** `http://localhost:8080/swagger-ui.html`

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/v1/usuarios` | Cadastrar novo usuário | 201, 409, 422 |
| `PUT` | `/v1/usuarios/{id}` | Atualizar dados (exceto senha) | 200, 404, 409, 422 |
| `DELETE` | `/v1/usuarios/{id}` | Excluir usuário | 204, 404 |
| `GET` | `/v1/usuarios?nome=...` | Buscar por nome (parcial) | 200 |
| `PATCH` | `/v1/usuarios/{id}/senha` | Trocar senha | 204, 400, 404, 422 |
| `POST` | `/v1/login` | Autenticar usuário | 200, 400, 404 |

**Formato de erro (RFC 7807):**
```json
{
  "type": "https://e-restaurante.com/errors/conflict",
  "title": "Conflito de dados",
  "status": 409,
  "detail": "Email já cadastrado: joao@email.com",
  "instance": "/v1/usuarios"
}
```

### 5.4 Como Executar o Projeto

**Pré-requisitos:** Docker e Docker Compose instalados.

```bash
# Clonar o repositório
git clone [URL_DO_REPOSITORIO]
cd e-restaurante

# Subir banco de dados e aplicação
docker compose up --build

# A aplicação estará disponível em:
# API:     http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

**Variáveis de ambiente (configuráveis no docker-compose.yml):**

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `postgres` | Host do banco de dados |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `erestaurante` | Nome do banco |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `JAVA_OPTS` | `-Xms256m -Xmx512m -XX:+UseZGC` | Opções da JVM |

---

## 6. Implementação

### 6.1 Linguagem e Práticas

O sistema foi implementado em **Java 25** com **Spring Boot 4.1.1**, seguindo os princípios **SOLID** e as práticas de **Domain-Driven Design**. A seguir, descrevemos como cada princípio SOLID se manifesta no código.

**S — Single Responsibility Principle:**
Cada classe tem uma única razão para mudar. `TrocarSenhaUseCaseImpl` cuida exclusivamente da troca de senha — o endpoint `PUT /v1/usuarios/{id}` nunca toca na senha. `GlobalExceptionHandler` é responsável apenas por mapear exceções para respostas HTTP. `UsuarioPersistenceMapper` apenas converte entre domain model e entidade JPA.

**O — Open/Closed Principle:**
O `GlobalExceptionHandler` está aberto para extensão (novas exceções de domínio herdam das classes base e são capturadas automaticamente) e fechado para modificação (não é necessário adicionar novos `@ExceptionHandler` para cada nova exceção). Para adicionar um novo tipo de usuário na Fase 2, cria-se uma nova subclasse de `Usuario` sem modificar os use cases existentes.

**L — Liskov Substitution Principle:**
`Cliente` e `DonoRestaurante` substituem `Usuario` em qualquer contexto sem quebrar comportamento. Os use cases operam sobre `Usuario` e retornam `Usuario`. Nenhum controller faz verificação de tipo com `instanceof` — o tipo é relevante apenas na camada de persistência (discriminator) e na instanciação (factory switch em `CadastrarUsuarioUseCaseImpl`).

**I — Interface Segregation Principle:**
Em vez de uma única interface `UsuarioService` com todos os métodos, foram criadas 6 interfaces segregadas por responsabilidade. O `LoginController` injeta apenas `AutenticarUsuarioUseCase`. O `UsuarioController` injeta apenas as interfaces dos endpoints que expõe. Mudanças em um caso de uso não forçam recompilação de classes que não o utilizam.

**D — Dependency Inversion Principle:**
O domínio define `UsuarioRepositoryPort` (abstração). A infraestrutura implementa com `UsuarioRepositoryAdapter` (detalhe concreto). A direção do código vai de `infrastructure → domain`, mas o fluxo de controle em runtime vai de `controller → use case → adapter → JPA`. Isso é a inversão: o detalhe depende da abstração, nunca o contrário.

### 6.2 Fluxo Implementado: Cadastro de Usuário

Este é o fluxo mais representativo do sistema, pois percorre todas as camadas da arquitetura.

**Requisição HTTP:**
```
POST /v1/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "login": "joaosilva",
  "senha": "senha123",
  "tipo": "CLIENTE",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "42",
    "complemento": "Apto 101",
    "bairro": "Jardim Paulista",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  }
}
```

**Passo a passo pela arquitetura:**

```
1. UsuarioController.cadastrar()
   │  valida a requisição com Bean Validation (@Valid)
   │  converte CadastrarUsuarioRequest → CadastrarUsuarioUseCase.Command (via UsuarioWebMapper)
   │
   ▼
2. CadastrarUsuarioUseCaseImpl.execute(Command)
   │  verifica se email já existe: UsuarioRepositoryPort.existePorEmail()
   │  → se sim: lança EmailJaCadastradoException (HTTP 409)
   │  criptografa a senha com BCryptPasswordEncoder
   │  instancia Cliente ou DonoRestaurante conforme command.tipo()
   │
   ▼
3. UsuarioRepositoryAdapter.salvar(Usuario)
   │  converte domain model → UsuarioEntity (via UsuarioPersistenceMapper)
   │  chama UsuarioRepository.save() (Spring Data JPA)
   │  @PrePersist seta dataUltimaAlteracao automaticamente
   │  converte UsuarioEntity → domain model e retorna
   │
   ▼
4. UsuarioController recebe o Usuario salvo
   │  converte para UsuarioResponse (via UsuarioWebMapper)
   │  retorna HTTP 201 Created com o corpo da resposta
```

**Cenário de erro — email duplicado:**

```
2. CadastrarUsuarioUseCaseImpl detecta email existente
   │  lança EmailJaCadastradoException (extends ConflictException)
   │
   ▼
GlobalExceptionHandler.handleConflict()
   │  captura ConflictException (classe base)
   │  cria ProblemDetail com status 409
   │  retorna application/problem+json
```

### 6.3 Fluxo Implementado: Troca de Senha

**Decisão de design:** A troca de senha possui endpoint próprio (`PATCH /v1/usuarios/{id}/senha`) separado da atualização geral, por duas razões: (1) as regras de validação são distintas — exige a senha atual; (2) manter a senha no endpoint geral criaria lógica condicional e violaria o SRP.

```
PATCH /v1/usuarios/{id}/senha
{
  "senhaAtual": "senha123",
  "novaSenha": "novaSenha456"
}

1. TrocarSenhaUseCaseImpl.execute(id, Command)
   │  busca usuário por id → UsuarioNaoEncontradoException se ausente
   │  BCryptPasswordEncoder.matches(senhaAtual, hashArmazenado)
   │  → se não bater: lança SenhaInvalidaException (HTTP 400)
   │  codifica novaSenha com BCrypt
   │  salva usuário com nova senha
   │
   ▼
   HTTP 204 No Content
```

---

## 7. Considerações Finais

### 7.1 Reflexão sobre Event Storming e DDD

O Event Storming revelou-se valioso mesmo para um domínio aparentemente simples como Usuário. A prática de nomear explicitamente os eventos (`UsuarioCadastrado`, `SenhaAlterada`) e os comandos que os disparam tornou as responsabilidades de cada use case evidentes antes mesmo de escrever a primeira linha de código. A decisão de separar `TrocarSenha` do `AtualizarUsuario`, por exemplo, emergiu naturalmente do mapeamento de eventos — cada evento tem um conjunto diferente de regras de negócio.

O DDD reforça que a qualidade de um sistema começa na modelagem, não na escolha de tecnologia. A estrutura de pacotes do projeto espelha o modelo de domínio, tornando o código legível para qualquer desenvolvedor que entenda o negócio, independentemente de conhecer Spring ou JPA.

### 7.2 Desafios Encontrados

**Compatibilidade de versões — Java 25 + Spring Boot 4 + Gradle:**
O principal desafio técnico foi a combinação de tecnologias de ponta. O Gradle 8.14.3 não suportava Java 25 como JVM de execução, e o plugin `io.spring.dependency-management` — padrão no Spring Boot 3.x — falhava ao parsear a versão do Java. A solução foi atualizar para **Gradle 9.7.1** (que suporta Java 25) e substituir o plugin pelo mecanismo nativo do Gradle (`platform()` BOM), alinhado com a abordagem recomendada para Spring Boot 4.

**Configuração do Spring Security sem autenticação real:**
Incluir o Spring Security apenas para obter o `BCryptPasswordEncoder` sem ativar seus filtros HTTP exigiu uma `SecurityConfig` explícita desabilitando CSRF e liberando todas as rotas. Isso é documentado como estado temporário — a autenticação real com JWT será implementada nas fases seguintes.

**MapStruct com herança polimórfica:**
O mapeamento entre `Usuario` (domínio) e `UsuarioEntity` (JPA) exigiu métodos `default` na interface MapStruct para despachar para o mapper correto conforme o tipo concreto (`instanceof` pattern matching do Java 21+), já que o MapStruct não resolve herança polimórfica automaticamente.

### 7.3 Benefícios Esperados

**Para o negócio:**
- Base sólida para escalar o sistema para os domínios de Restaurante e Pedido sem retrabalho
- Segurança desde o início com hashing BCrypt (sem senhas em texto puro)
- API documentada e testável via Swagger UI, facilitando integração com frontend

**Para a equipe:**
- Arquitetura hexagonal como laboratório prático de SOLID e DDD — conhecimento aplicável em qualquer projeto corporativo
- Código testável por design: os use cases em Java puro podem ser testados em milissegundos, sem subir Spring context
- Histórico de schema controlado pelo Liquibase, eliminando divergências entre ambientes

**Para a avaliação:**
- Separação clara de responsabilidades demonstra domínio dos princípios SOLID
- Decisões arquiteturais justificadas em termos de negócio, não apenas de tecnologia
- Stack atualizada (Java 25 LTS + Spring Boot 4) demonstra acompanhamento do ecossistema

---

## 8. Artefatos

| Artefato | Tipo | Localização |
|---|---|---|
| Código-fonte da aplicação | Repositório Git | [URL_DO_REPOSITORIO] |
| Este relatório técnico | PDF | Submetido na plataforma FIAP |
| Swagger UI (interativo) | Web | `http://localhost:8080/swagger-ui.html` (com app rodando) |
| OpenAPI spec (JSON) | Endpoint | `http://localhost:8080/v3/api-docs` (com app rodando) |
| Postman Collection | JSON | `/postman/e-restaurante.postman_collection.json` no repositório |
| Migration de banco | YAML | `src/main/resources/db/changelog/` no repositório |
| Dockerfile multi-stage | Docker | Raiz do repositório |
| Docker Compose | Docker | Raiz do repositório |

**Instruções para execução:**
```bash
git clone [URL_DO_REPOSITORIO]
cd e-restaurante
docker compose up --build
# Aguardar log: "Started ERestauranteApplication"
# Acessar: http://localhost:8080/swagger-ui.html
```

---

## 9. Referências

- EVANS, Eric. **Domain-Driven Design: Tackling Complexity in the Heart of Software**. Addison-Wesley, 2003.

- BRANDOLINI, Alberto. **Introducing Event Storming**. Leanpub, 2021. Disponível em: https://www.eventstorming.com

- MARTIN, Robert C. **Clean Architecture: A Craftsman's Guide to Software Structure and Design**. Prentice Hall, 2017.

- COCKBURN, Alistair. **Hexagonal Architecture**. 2005. Disponível em: https://alistair.cockburn.us/hexagonal-architecture

- MARTIN, Robert C. **Agile Software Development: Principles, Patterns, and Practices** (SOLID). Prentice Hall, 2002.

- Spring Boot Reference Documentation v4.1.1. Disponível em: https://docs.spring.io/spring-boot/docs/4.1.1/reference/html

- Liquibase Documentation. Disponível em: https://docs.liquibase.com

- MapStruct Reference Guide 1.6.3. Disponível em: https://mapstruct.org/documentation/stable/reference/html

- RFC 7807 — Problem Details for HTTP APIs. IETF, 2016. Disponível em: https://datatracker.ietf.org/doc/html/rfc7807

- OpenAPI Specification 3.0. Disponível em: https://swagger.io/specification
