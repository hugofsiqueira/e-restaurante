# e-Restaurante: Sistema de Gestão para Restaurantes
## Relatório Técnico - Fase 2: Tipos de Usuário, Restaurante e Cardápio

**Autor:**
- Hugo Luís de França Siqueira - RM374343

**Equipe:** Individual  
**Data:** 31/10/2026  
**Instituição:** FIAP (Faculdade de Informática e Administração Paulista)

---

## 1. Introdução

Este documento descreve as decisões arquiteturais, implementações e resultados da **Fase 2** do Tech Challenge de Arquitetura e Desenvolvimento Java — projeto **e-Restaurante**.

A Fase 1 estabeleceu a fundação do sistema com o domínio de Usuário (cadastro, autenticação e gerenciamento de perfis). Esta Fase 2 expande o sistema em três frentes:

1. **TipoUsuario** — migração de enum para entidade de domínio gerenciável via CRUD
2. **Restaurante** — cadastro, consulta, atualização e exclusão de estabelecimentos com seus donos e horários
3. **ItemCardapio** — gerenciamento do cardápio de cada restaurante com preço, disponibilidade e foto

A entrega é sustentada por **119 testes automatizados** (100% de cobertura de linhas e branches no escopo de domínio), Liquibase para controle de schema, e coleção Postman completa com 7 grupos de cenários.

---

## 2. Evolução da Arquitetura: Clean Arch Intermediário

### 2.1 Padrão adotado

A Fase 2 mantém e aprofunda o padrão chamado neste projeto de **Clean Arch Intermediário**, que se diferencia da arquitetura hexagonal clássica por introduzir um orquestrador de domínio em Java puro — o `Controller` de domínio — entre os casos de uso e o adaptador HTTP do Spring.

```
┌─────────────────────────────────────────────────────────────────┐
│  INFRA / WEB (Spring)                                           │
│                                                                 │
│  RestauranteApi (@RestController)                               │
│       │                                                         │
│       │  delega via                                             │
│       ▼                                                         │
│  ┌─────────────────────────────────────────────────┐           │
│  │  CORE (Java puro — zero anotações Spring)        │           │
│  │                                                  │           │
│  │  RestauranteController                           │           │
│  │       │                                          │           │
│  │       ├──► CriarRestauranteUseCaseImpl           │           │
│  │       ├──► BuscarRestauranteUseCaseImpl          │           │
│  │       ├──► AtualizarRestauranteUseCaseImpl       │           │
│  │       └──► ExcluirRestauranteUseCaseImpl         │           │
│  │                │                                 │           │
│  │                ▼                                 │           │
│  │         RestauranteGateway (interface)           │           │
│  └─────────────────────────────────────────────────┘           │
│       │                                                         │
│       ▼                                                         │
│  RestauranteJpaGateway                                          │
│       │                                                         │
│       ▼                                                         │
│  RestauranteRepository (Spring Data JPA)                        │
│       │                                                         │
│       ▼                                                         │
│  PostgreSQL                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Vantagem do Controller de domínio:** ele orquestra múltiplos casos de uso sem depender do Spring, tornando a lógica de composição testável com Mockito puro (sem `@SpringBootTest`). O `@RestController` torna-se um adaptador fino que apenas traduz HTTP → Command → HTTP.

### 2.2 Estrutura de pacotes da Fase 2

```
br.com.fiap.erestaurante/
│
├── core/                                    ← Java puro, zero dependência de framework
│   ├── domain/
│   │   ├── TipoUsuario.java                 ← entidade (antes era enum)
│   │   ├── Restaurante.java
│   │   ├── Endereco.java                    ← value object reutilizado
│   │   ├── HorarioFuncionamento.java        ← novo value object
│   │   └── ItemCardapio.java
│   ├── gateway/
│   │   ├── TipoUsuarioGateway.java
│   │   ├── RestauranteGateway.java
│   │   └── ItemCardapioGateway.java
│   ├── usecase/
│   │   ├── tipousuario/
│   │   ├── restaurante/
│   │   └── itemcardapio/
│   ├── controller/
│   │   ├── TipoUsuarioController.java
│   │   ├── RestauranteController.java
│   │   ├── ItemCardapioController.java
│   │   └── UsuarioController.java
│   └── exception/
│       ├── TipoUsuarioNaoEncontradoException.java
│       ├── TipoUsuarioJaCadastradoException.java
│       ├── RestauranteNaoEncontradoException.java
│       ├── DonoRestauranteInvalidoException.java
│       └── ItemCardapioNaoEncontradoException.java
│
└── infra/
    ├── database/
    │   ├── entity/         ← entidades JPA (RestauranteEntity, ItemCardapioEntity, etc.)
    │   ├── mapper/         ← MapStruct persistence mappers
    │   ├── jpa/            ← implementações dos Gateways
    │   └── repository/     ← interfaces Spring Data JPA
    ├── web/
    │   ├── json/           ← DTOs de entrada e saída com Bean Validation
    │   ├── mapper/         ← MapStruct web mappers
    │   ├── TipoUsuarioApi.java
    │   ├── RestauranteApi.java
    │   └── ItemCardapioApi.java
    └── config/
        └── InjecaoDependenciaConfiguration.java  ← wiring explícito de todos os beans
```

---

## 3. Domínios Implementados

### 3.1 TipoUsuario

**Mudança em relação à Fase 1:** `TipoUsuario` era um enum fixo no código (`CLIENTE`, `DONO_RESTAURANTE`). Na Fase 2 ele foi promovido a **entidade de domínio** com CRUD completo, permitindo que novos tipos sejam criados e gerenciados em tempo de execução sem alteração no código.

**Entidade de domínio:**

| Campo | Tipo | Restrições |
|---|---|---|
| `id` | `Long` | Gerado pelo banco |
| `nome` | `String` | Obrigatório, único (case-insensitive), máx. 100 chars |

**Regras de negócio:**
- Nome de tipo deve ser único no sistema (validado no use case, antes de persistir)
- Exclusão de um tipo que esteja vinculado a usuários é rejeitada pelo banco (FK constraint)

**Endpoints:**

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/v1/tipos-usuario` | Criar tipo | 201, 409, 422 |
| `GET` | `/v1/tipos-usuario` | Listar todos | 200 |
| `GET` | `/v1/tipos-usuario/{id}` | Buscar por ID | 200, 404 |
| `PUT` | `/v1/tipos-usuario/{id}` | Atualizar nome | 200, 404, 409, 422 |
| `DELETE` | `/v1/tipos-usuario/{id}` | Excluir | 204, 404 |

**Impacto na entidade Usuario:** o campo `tipo` (enum) foi substituído por `tipoUsuario` (FK para `tipo_usuario`). A `CadastrarUsuarioUseCaseImpl` busca o `TipoUsuario` pelo ID informado no comando e verifica se o nome é `DONO_RESTAURANTE` para decidir qual subtipo concreto instanciar.

### 3.2 Restaurante

**Modelo de domínio:**

| Campo | Tipo | Restrições |
|---|---|---|
| `id` | `Long` | Gerado pelo banco |
| `nome` | `String` | Obrigatório, máx. 200 chars |
| `tipoCozinha` | `String` | Obrigatório, máx. 100 chars |
| `endereco` | `Endereco` | Value object, todos os subcampos opcionais exceto logradouro e número |
| `horarioFuncionamento` | `HorarioFuncionamento` | Value object com `abertura` e `fechamento` (`LocalTime`) |
| `dono` | `DonoRestaurante` | Obrigatório, FK para tabela `usuario` |

**Regras de negócio:**
- O usuário vinculado como dono deve existir (`UsuarioNaoEncontradoException` → HTTP 404)
- O usuário vinculado deve ser do tipo `DonoRestaurante` (`DonoRestauranteInvalidoException` → HTTP 400)
- Busca por nome é parcial e case-insensitive

**Endpoints:**

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/v1/restaurantes` | Criar restaurante | 201, 400, 404, 422 |
| `GET` | `/v1/restaurantes?nome=...` | Buscar por nome (parcial) | 200 |
| `GET` | `/v1/restaurantes/{id}` | Buscar por ID | 200, 404 |
| `PUT` | `/v1/restaurantes/{id}` | Atualizar dados | 200, 404, 422 |
| `DELETE` | `/v1/restaurantes/{id}` | Excluir | 204, 404 |

**Decisão de modelagem — Endereco embutido na tabela restaurante:**

O `Endereco` é persistido com prefixo `end_` nas colunas da tabela `restaurante` via `@AttributeOverrides`, evitando conflito de nomes e mantendo o schema em uma única tabela sem JOIN.

```sql
-- Colunas geradas pela anotação @AttributeOverrides
end_logradouro, end_numero, end_complemento, end_bairro,
end_cidade, end_estado, end_cep
```

**Decisão de modelagem — HorarioFuncionamento como @Embeddable:**

```java
@Embeddable
public class HorarioFuncionamentoEntity {
    @Column(name = "hora_abertura") LocalTime abertura;
    @Column(name = "hora_fechamento") LocalTime fechamento;
}
```

Ambos os value objects seguem o padrão de **imutabilidade**: não têm setters, são construídos via `@Builder`/`@AllArgsConstructor` e nunca são modificados diretamente — substituídos integralmente em uma atualização.

### 3.3 ItemCardapio

**Modelo de domínio:**

| Campo | Tipo | Restrições |
|---|---|---|
| `id` | `Long` | Gerado pelo banco |
| `nome` | `String` | Obrigatório, máx. 200 chars |
| `descricao` | `String` | Opcional, até 2000 chars (TEXT no banco) |
| `preco` | `BigDecimal` | Obrigatório, mínimo 0,01, precisão (10, 2) |
| `disponivel` | `boolean` | Padrão: `true` |
| `fotoPath` | `String` | Opcional, caminho para arquivo, máx. 500 chars |
| `restaurante` | `Restaurante` | Obrigatório, FK para tabela `restaurante` |

**Regras de negócio:**
- O restaurante informado deve existir (`RestauranteNaoEncontradoException` → HTTP 404)
- A consulta de itens é feita por restaurante (não há busca global de itens)
- O campo `disponivel` permite marcar itens temporariamente indisponíveis sem excluí-los

**Endpoints:**

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/v1/itens-cardapio` | Adicionar item | 201, 404, 422 |
| `GET` | `/v1/itens-cardapio?restauranteId=X` | Listar por restaurante | 200 |
| `GET` | `/v1/itens-cardapio/{id}` | Buscar por ID | 200, 404 |
| `PUT` | `/v1/itens-cardapio/{id}` | Atualizar item | 200, 404, 422 |
| `DELETE` | `/v1/itens-cardapio/{id}` | Excluir item | 204, 404 |

---

## 4. Decisões Arquiteturais da Fase 2

### DA-07: Builder Pattern com @SuperBuilder

**Decisão:** Aplicar `@Builder` (Lombok) em todas as entidades de domínio e `@SuperBuilder` na hierarquia `Usuario → Cliente / DonoRestaurante`.

**Justificativa:** Construtores posicionais com muitos parâmetros são frágeis: a inserção de um novo campo no meio da lista quebra silenciosamente todos os pontos de construção. O builder nomeado elimina esse acoplamento posicional. O `@SuperBuilder` é necessário porque o Lombok não consegue herdar builders gerados com `@Builder` simples — `@SuperBuilder` gera implementações de builder para cada nível da hierarquia que são compatíveis entre si.

**Consequência:** Todos os objetos de domínio são criados com sintaxe fluente:

```java
var restaurante = Restaurante.builder()
    .nome("Cantina da Maria")
    .tipoCozinha("Italiana")
    .dono(DonoRestaurante.builder().id(1L).build())
    .horarioFuncionamento(HorarioFuncionamento.builder()
        .abertura(LocalTime.of(11, 0))
        .fechamento(LocalTime.of(23, 0))
        .build())
    .build();
```

### DA-08: Gateway como porta de saída no lugar de Port/Adapter

**Decisão:** Renomear a abstração de persistência de `*RepositoryPort` (Fase 1) para `*Gateway` (Fase 2).

**Justificativa:** O termo `Gateway` é mais expressivo na linguagem DDD para contratos que abstraem recursos externos (banco de dados, APIs, filas). `RepositoryPort` é um nome técnico que mistura a nomenclatura de Ports & Adapters com a de Spring Data Repository. O `Gateway` é definido no `core/` (Java puro) e implementado no `infra/database/jpa/`.

### DA-09: Referência FK sem carregar a entidade completa nos mappers

**Decisão:** Nos mappers de persistência (`RestaurantePersistenceMapper`, `ItemCardapioPersistenceMapper`), a conversão do objeto de domínio para entidade JPA para campos de relacionamento cria um objeto entidade apenas com o `id` preenchido.

```java
default DonoRestauranteEntity donoToEntity(DonoRestaurante dono) {
    if (dono == null) return null;
    var entity = new DonoRestauranteEntity();
    entity.setId(dono.getId());
    return entity;
}
```

**Justificativa:** O JPA usa o objeto com apenas o `id` como referência proxy para gerar o `INSERT`/`UPDATE` com o valor da FK. Isso evita um `SELECT` adicional para carregar o `DonoRestauranteEntity` completo apenas para persistir uma referência de chave estrangeira — otimização de n+1 na escrita.

### DA-10: Changelogs Liquibase organizados por ano/mês

**Decisão:** Os arquivos de changeset seguem a estrutura `db/changelog/changelogs/{ano}/{mês}/NNN-descricao.yaml`. O master usa `includeAll` em vez de `include` individual.

```
changelogs/
├── 2026/09/
│   └── 001-create-usuario-table.yaml
└── 2026/10/
    ├── 002-create-tipo-usuario-table.yaml
    ├── 003-create-restaurante-table.yaml
    └── 004-create-item-cardapio-table.yaml
```

**Justificativa:** A organização por data reflete a cronologia real das migrações, facilita auditoria ("o que foi criado em outubro?") e o `includeAll` garante que novos arquivos adicionados ao diretório correto são descobertos automaticamente, sem necessidade de registrar cada arquivo no master.

### DA-11: Exclusão de classes de infraestrutura do relatório de cobertura

**Decisão:** As classes `*JpaGateway`, `*PersistenceMapper`, entidades JPA, DTOs e `LoginApi` são excluídas do relatório JaCoCo.

**Justificativa:** Essas classes são adaptadores de infraestrutura — seu comportamento é uma consequência direta do framework (Spring Data JPA, MapStruct). Cobrí-las com testes unitários exigiria mocks de `EntityManager` ou `SqlSession`, o que testa a infraestrutura, não a regra de negócio. A cobertura significativa é a do `core/`: domínio, gateways, use cases e controllers — todos em Java puro.

---

## 5. Controle de Schema: Liquibase

### Changeset 001 — Tabela usuario (Fase 1)

```yaml
# changelogs/2026/09/001-create-usuario-table.yaml
createTable: usuario
  id BIGINT (PK, auto-increment)
  dtype VARCHAR(31)         -- discriminador SINGLE_TABLE
  nome VARCHAR(150)
  email VARCHAR(255) UNIQUE
  login VARCHAR(100) UNIQUE
  senha VARCHAR(255)
  data_ultima_alteracao TIMESTAMP
  -- colunas de endereco embutidas
  logradouro, numero, complemento, bairro, cidade, estado, cep
```

### Changeset 002 — Tabela tipo_usuario + FK em usuario

```yaml
# changelogs/2026/10/002-create-tipo-usuario-table.yaml
createTable: tipo_usuario
  id BIGINT (PK, auto-increment)
  nome VARCHAR(100) UNIQUE

# Seeds iniciais
INSERT INTO tipo_usuario (nome) VALUES ('CLIENTE'), ('DONO_RESTAURANTE')

# Alteração na tabela usuario
addColumn: tipo_usuario_id BIGINT NOT NULL
addForeignKeyConstraint: fk_usuario_tipo_usuario → tipo_usuario(id)
```

### Changeset 003 — Tabela restaurante

```yaml
# changelogs/2026/10/003-create-restaurante-table.yaml
createTable: restaurante
  id, nome, tipo_cozinha
  hora_abertura TIME, hora_fechamento TIME
  end_logradouro, end_numero, end_complemento,
  end_bairro, end_cidade, end_estado, end_cep
  dono_id BIGINT → FK para usuario(id)

Indexes: idx_restaurante_nome, idx_restaurante_dono
```

### Changeset 004 — Tabela item_cardapio

```yaml
# changelogs/2026/10/004-create-item-cardapio-table.yaml
createTable: item_cardapio
  id, nome VARCHAR(200)
  descricao TEXT
  preco DECIMAL(10,2)
  disponivel BOOLEAN DEFAULT true
  foto_path VARCHAR(500)
  restaurante_id BIGINT → FK para restaurante(id)

Indexes: idx_item_cardapio_restaurante, idx_item_cardapio_nome
```

---

## 6. Cobertura de Testes

### 6.1 Configuração JaCoCo

O JaCoCo foi configurado no `build.gradle.kts` como gate obrigatório no ciclo `check`:

```kotlin
plugins {
    jacoco
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit { counter = "LINE";   value = "COVEREDRATIO"; minimum = "0.80".toBigDecimal() }
            limit { counter = "BRANCH"; value = "COVEREDRATIO"; minimum = "0.80".toBigDecimal() }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
```

Se a cobertura cair abaixo de 80% em linhas ou branches, `./gradlew check` falha — impedindo que código não testado seja integrado.

### 6.2 Resultados

| Contador | Cobertas | Total | Cobertura |
|---|---|---|---|
| **LINE** | 410 | 410 | **100%** |
| **BRANCH** | 26 | 26 | **100%** |
| **METHOD** | 163 | 163 | **100%** |
| **Testes totais** | 119 | 119 | **0 falhas** |

### 6.3 Distribuição dos testes

| Grupo | Tipo | Qtd. | O que cobre |
|---|---|---|---|
| `usecase/usuario/` | Unitário (Mockito) | 16 | Todos os use cases de usuario |
| `usecase/tipousuario/` | Unitário (Mockito) | 10 | Todos os use cases de tipo usuario |
| `usecase/restaurante/` | Unitário (Mockito) | 12 | Todos os use cases de restaurante |
| `usecase/itemcardapio/` | Unitário (Mockito) | 10 | Todos os use cases de item cardápio |
| `controller/` | Unitário (Mockito) | 22 | Delegação nos 4 controllers de domínio |
| `infra/web/UsuarioApiTest` | Integração (@SpringBootTest) | 17 | Camada HTTP do domínio usuario |
| `infra/web/TipoUsuarioApiTest` | Integração (@SpringBootTest) | 12 | Camada HTTP do domínio tipo usuario |
| `infra/web/RestauranteApiTest` | Integração (@SpringBootTest) | 10 | Camada HTTP do domínio restaurante |
| `infra/web/ItemCardapioApiTest` | Integração (@SpringBootTest) | 10 | Camada HTTP do domínio item cardápio |

**Estratégia de teste em dois níveis:**

- **Unitários (Mockito puro):** testam use cases e controllers isoladamente, sem Spring. Rápidos (< 100ms cada), cobrem regras de negócio e todos os caminhos de exceção.
- **Integração (@SpringBootTest + @MockitoBean):** sobem o contexto Spring com H2 em memória, mockam o controller de domínio, testam a camada HTTP (deserialização JSON, Bean Validation, mapeamento de exceções para ProblemDetail, status HTTP corretos).

---

## 7. Coleção Postman

A coleção `postman/e-restaurante.postman_collection.json` cobre os 7 grupos de cenários:

| Pasta | Cenários | Descrição |
|---|---|---|
| 1. Usuários | 9 | Cadastro, atualização, busca, exclusão; cenários de erro |
| 2. Senha | 4 | Troca de senha com validação da senha atual |
| 3. Login | 3 | Autenticação com credenciais válidas e inválidas |
| 4. Excluir usuário | 2 | Exclusão com 204 e 404 |
| 5. Tipos de Usuário | 9 | CRUD completo + cenários de erro (duplicidade, inexistente) |
| 6. Restaurantes | 10 | CRUD + busca por nome + validações de dono |
| 7. Itens de Cardápio | 10 | CRUD + busca por restaurante + disponibilidade + validações |

**Variáveis de coleção:** `baseUrl`, `usuarioId`, `tipoClienteId`, `tipoDonoId`, `restauranteId`, `donoId`, `itemCardapioId`

Cada request inclui scripts `pm.test` que validam status HTTP e campos relevantes do corpo da resposta, permitindo executar a coleção completa no Postman Runner.

---

## 8. Stack Tecnológica

*(Mantida da Fase 1 — sem alterações de versão)*

| Componente | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 25 |
| Framework | Spring Boot | 4.1.1 |
| Build | Gradle | 9.7.1 (Kotlin DSL) |
| Banco de dados | PostgreSQL | 16 |
| Migrations | Liquibase | (BOM Spring Boot) |
| ORM | Spring Data JPA / Hibernate | (BOM) |
| Mapeamento | MapStruct | 1.6.3 |
| Boilerplate | Lombok | (BOM) |
| Documentação | SpringDoc OpenAPI | 2.8.9 |
| Cobertura | JaCoCo | (plugin Gradle nativo) |

---

## 9. Como Executar

**Pré-requisitos:** Docker e Docker Compose instalados.

```bash
git clone https://github.com/hugofsiqueira/e-restaurante
cd e-restaurante

# Subir banco + aplicação
docker compose up --build

# API:     http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

**Executar testes e relatório de cobertura:**

```bash
./gradlew test jacocoTestReport

# Relatório HTML gerado em:
# build/reports/jacoco/test/html/index.html
```

**Verificar cobertura mínima (falha se < 80%):**

```bash
./gradlew check
```

---

## 10. Artefatos

| Artefato | Localização |
|---|---|
| Código-fonte | https://github.com/hugofsiqueira/e-restaurante |
| Vídeo de apresentação (5 min) | https://drive.google.com/file/d/1AY9iLM6qmQv4KHTRzgBWFUpHN6Q4Be-k/view?usp=sharing |
| Relatório Fase 1 (PDF) | Submetido na plataforma FIAP |
| Relatório Fase 2 (este documento) | Submetido na plataforma FIAP |
| Swagger UI (interativo) | `http://localhost:8080/swagger-ui.html` (com app rodando) |
| OpenAPI spec | `http://localhost:8080/v3/api-docs` |
| Coleção Postman | `postman/e-restaurante.postman_collection.json` |
| Changelogs Liquibase | `src/main/resources/db/changelog/changelogs/` |
| Relatório JaCoCo | `build/reports/jacoco/test/html/index.html` (após `./gradlew test`) |

### 10.1 Vídeo de Apresentação

> **URL:** https://drive.google.com/file/d/1AY9iLM6qmQv4KHTRzgBWFUpHN6Q4Be-k/view?usp=sharing

O vídeo de até 5 minutos demonstra, com código e aplicação em execução:

1. Estrutura do projeto e arquitetura Clean Arch Intermediário
2. Estratégia de associação entre `Usuario` e `TipoUsuario` (FK + SINGLE_TABLE)
3. Docker Compose com PostgreSQL e Liquibase
4. Swagger UI com todos os endpoints documentados
5. Execução da coleção Postman no Runner (todos os testes passando)
6. Relatório de cobertura JaCoCo — 100% no escopo de domínio

---

## 11. Considerações Finais

### 11.1 O que a Fase 2 consolidou

A Fase 1 provou a viabilidade da arquitetura. A Fase 2 a colocou em escala: três novos domínios foram adicionados sem modificar o código existente de Usuário — exceto a troca de enum para entidade de `TipoUsuario`, que foi uma refatoração localizada e não quebrou nenhum caso de uso.

O padrão Clean Arch Intermediário mostrou seu valor: cada novo domínio seguiu o mesmo template (Gateway → UseCase → Controller → Api), os testes foram escritos no mesmo estilo e o `InjecaoDependenciaConfiguration` cresceu de forma previsível.

### 11.2 Desafios

**@SuperBuilder na hierarquia de herança:** Lombok não gera builders compatíveis com herança quando se usa `@Builder` simples. A mudança para `@SuperBuilder` exigiu remover `@AllArgsConstructor` das subclasses (pois `@SuperBuilder` gera seu próprio construtor protegido), o que quebrou testes que usavam construtores posicionais. A correção foi migrar todos os pontos de construção para o estilo builder.

**Referências FK sem carregamento desnecessário:** O mapper de persistência precisava converter `DonoRestaurante` → `DonoRestauranteEntity` para persistir a FK do restaurante. A solução de criar um objeto entidade apenas com o `id` preenchido é idiomática no JPA (proxy por id), mas não intuitiva para quem não conhece o comportamento do EntityManager.

### 11.3 Próximas fases

Com TipoUsuario, Restaurante e ItemCardapio implementados, a Fase 3 poderá focar nos domínios de **Pedido** e **Avaliação**, que dependem do relacionamento entre Cliente, Restaurante e ItemCardapio — todos já presentes e testados.

---

## 12. Referências

- EVANS, Eric. **Domain-Driven Design: Tackling Complexity in the Heart of Software**. Addison-Wesley, 2003.

- MARTIN, Robert C. **Clean Architecture: A Craftsman's Guide to Software Structure and Design**. Prentice Hall, 2017.

- COCKBURN, Alistair. **Hexagonal Architecture**. 2005. Disponível em: https://alistair.cockburn.us/hexagonal-architecture

- BRANDOLINI, Alberto. **Introducing Event Storming**. Leanpub, 2021. Disponível em: https://www.eventstorming.com

- Lombok `@SuperBuilder` documentation. Disponível em: https://projectlombok.org/features/experimental/SuperBuilder

- JaCoCo — Java Code Coverage Library. Disponível em: https://www.jacoco.org/jacoco

- Spring Boot Reference Documentation v4.1.1. Disponível em: https://docs.spring.io/spring-boot

- Liquibase Documentation. Disponível em: https://docs.liquibase.com

- MapStruct Reference Guide 1.6.3. Disponível em: https://mapstruct.org/documentation/stable/reference/html

- RFC 7807 - Problem Details for HTTP APIs. IETF, 2016. Disponível em: https://datatracker.ietf.org/doc/html/rfc7807
