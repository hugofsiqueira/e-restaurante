# Tutorial — Tech Challenge Fase 1 (Arquitetura e Desenvolvimento Java)

> Pessoal, juntei aqui as anotações que vocês tiraram no grupo de estudos (`Grupo_Estudos.png`) com o enunciado oficial do desafio, e organizei tudo na ordem em que vocês realmente vão executar o projeto. A ideia é que, lendo de cima para baixo, vocês saiam sabendo exatamente o que fazer em cada etapa — e, mais importante, *por que* cada etapa existe.

## Antes de começar: entendendo o problema

Vamos contextualizar rapidinho, porque isso muda a forma como vocês vão pensar a solução. Um grupo de restaurantes da região resolveu se unir e contratar um sistema de gestão único e compartilhado, em vez de cada um pagar por uma solução própria. Faz sentido do ponto de vista de negócio: sai mais barato dividir o custo de desenvolvimento entre vários restaurantes do que cada um bancar o seu sozinho.

O sistema completo, lá na frente, vai permitir que clientes consultem restaurantes, avaliem e façam pedidos. Só que, como o orçamento é limitado, a entrega vai ser feita em fases — e essa Fase 1 é a fundação. Por isso o print de vocês fala quase só de "Usuário": é literalmente o único domínio de negócio que essa fase pede. Não fiquem procurando "Restaurante" ou "Pedido" no requisito, porque isso é assunto de fase futura.

**Guardem essa data: 01/09 é o prazo de entrega.** E já adianto uma coisa importante que vocês vão entender melhor lá no passo 8: o que é avaliado oficialmente é um relatório em PDF, não o código em si. Então já pensem no cronograma de trás para frente a partir dessa data.

---

## Passo 1 — Modelando a entidade Usuário

Todo sistema começa pela modelagem, então é por aqui que a gente entra. A entidade `Usuario` precisa ter estes campos, independente do tipo de usuário:

| Campo | Tipo | O que reparar |
|---|---|---|
| `id` | identificador | gerado pelo banco |
| `nome` | String | — |
| `email` | String | **precisa ser único** — e atenção, essa validação tem que estar na camada de serviço, não só como constraint do banco |
| `login` | String | usado na hora do login |
| `senha` | String | vamos falar de como guardar isso com segurança no passo 3 |
| `endereco` | String **ou** objeto | pode ser um campo texto simples, ou uma entidade própria com rua, número, cidade, CEP |
| `dataUltimaAlteracao` | Date | atualizada toda vez que o registro é criado ou editado |

Sobre o endereço: o enunciado dá liberdade para vocês escolherem entre string simples ou entidade separada. Se o objetivo de vocês é também praticar modelagem relacional — usando `@OneToOne` ou `@Embeddable` do JPA — vale a pena optar pela entidade. É mais parecido com o que a gente vê em sistema real, onde endereço tem CEP, cidade, cada campo pesquisável e validável separadamente. Mas se o foco de vocês agora é fechar o escopo rápido, string simples resolve sem problema.

### E os dois tipos de usuário?

Aqui tem um requisito que **não é opcional**: o sistema precisa suportar dois papéis, **Cliente** e **Dono do Restaurante**.

A pergunta que sempre aparece nessa hora é: "uso herança ou não?" E a resposta curta é: as duas formas estão liberadas pelo enunciado. Não existe certo ou errado aqui — existe uma decisão de arquitetura que vocês vão precisar justificar no relatório técnico. Deixa eu dar uma luz sobre como pensar isso:

- Se vocês usarem **herança** — `Cliente` e `DonoRestaurante` estendendo `Usuario` — isso fica mais alinhado com orientação a objetos, principalmente pensando que, nas próximas fases, o dono provavelmente vai ganhar um vínculo com restaurantes e o cliente vai ganhar um histórico de pedidos. Ou seja, cada tipo tende a acumular comportamento próprio com o tempo.
- Se vocês usarem um **campo discriminador** (tipo um enum `TipoUsuario` dentro de uma única tabela `Usuario`), a implementação fica mais simples agora, com menos tabelas e menos joins.

Não precisam decidir isso de cabeça fria — pensem em qual caminho vocês conseguem sustentar bem quando a Fase 2 chegar com a entidade Restaurante.

---

## Passo 2 — Construindo os endpoints de Usuário

Agora que a entidade está modelada, bora para a API. O print de vocês resumiu isso em quatro operações mais a busca — vou destrinchar cada uma.

### O CRUD básico

- **Inclusão** — um `POST` simples para cadastrar usuário novo.
- **Atualização** — `PUT` ou `PATCH` para editar os dados do usuário, **menos a senha**. Já já explico por que ela fica de fora.
- **Exclusão** — `DELETE`.
- **Consulta por nome** — `GET /usuarios?name=Joao`. Reparem que é busca por nome, não por id, então a query no banco precisa ser um `LIKE` (ou `contains`), não uma igualdade exata. Se vocês implementarem como igualdade, a busca só vai funcionar quando o nome digitado for idêntico ao cadastrado, e isso não é uma "busca" de verdade.

### Por que a senha ganha um endpoint só dela

Reparem que o enunciado pede isso explicitamente:

```
POST /usuario/{usuarioId}/password
{
  "novaSenha": "...",
  "confirmacaoSenha": "..."
}
```

Vale a pena entender o porquê disso, porque é uma prática que vocês vão ver em qualquer API séria por aí: operações sensíveis, como troca de senha, costumam ficar isoladas do resto do cadastro. Primeiro porque as regras de validação são diferentes — aqui vocês precisam conferir se `novaSenha` bate com `confirmacaoSenha`, e talvez aplicar política de complexidade. Segundo porque, se esse campo estivesse junto do endpoint de atualização geral, qualquer PUT de "troquei meu endereço" correria o risco de mexer sem querer na senha, ou a lógica do método ficaria cheia de "if tem senha, faz isso, se não, faz aquilo". Separar deixa cada endpoint com uma responsabilidade só — e isso, não por acaso, é o princípio da responsabilidade única que vocês já viram em SOLID.

### O login

```
POST /login
{
  "usuario": "...",
  "senha": "..."
}
```

Duas coisas importantes aqui:

Primeiro, **Spring Security não é obrigatório** nesta fase. Vocês podem — e devem, para não perder tempo — fazer algo mais simples: um serviço que busca o usuário no banco e compara login e senha.

Segundo, ainda que não seja obrigatório, é fortemente recomendado **não salvar a senha em texto puro no banco**. Usem hash, por exemplo com BCrypt. O enunciado marca isso como boa prática, não como requisito obrigatório — mas pensem comigo: implementar hashing desde o início custa uma dependência e algumas linhas de código. Migrar depois, com usuários já cadastrados com senha em texto aberto, é bem mais trabalhoso. Então, mesmo não sendo nota, vale fazer certo desde já.

---

## Passo 3 — Não esqueçam da auditoria

Esse é rápido, mas é fácil de passar batido se vocês não pensarem nele desde a modelagem. Toda vez que um usuário for criado ou alterado, o campo `dataUltimaAlteracao` (que a gente já deixou reservado lá no passo 1) precisa ser atualizado.

Na prática, dá para resolver isso de forma automática usando os callbacks do JPA — `@PrePersist` para quando o registro é criado, `@PreUpdate` para quando é editado — em vez de ficar setando essa data manualmente em cada método do service. Fica menos sujeito a esquecimento.

---

## Passo 4 — Dois detalhes que não estão no print, mas contam ponto

Esses dois itens vocês não vão encontrar nas anotações do grupo de estudos, mas estão no enunciado oficial como critério de avaliação — então deixa eu chamar atenção para eles agora, antes que a implementação avance e fique mais caro corrigir depois:

- **Versionamento de API.** Definam uma estratégia desde o primeiro endpoint que vocês escreverem — o mais comum é um prefixo `/v1/` nas rotas. É uma decisão barata de tomar no início e cara de aplicar depois, quando já existem vários endpoints no ar.
- **Tratamento de erro padronizado com ProblemDetail (RFC 7807).** O Spring Boot 3 já traz suporte nativo a isso. A ideia é que erro de validação, e-mail duplicado, login inválido — tudo — volte num formato de resposta consistente. Isso ajuda tanto os testes de vocês quanto qualquer frontend que for consumir essa API no futuro.

---

## Passo 5 — Colocando tudo para rodar com Docker

### O Dockerfile

O enunciado pede uma abordagem **multi-stage**, ou seja, duas etapas dentro do mesmo Dockerfile:

1. **Estágio de build** — uma imagem com JDK e Maven (ou Gradle) que compila o projeto e gera o artefato, o `.jar`.
2. **Estágio de execução** — uma imagem enxuta, só com o JRE (Java Runtime, sem o toolchain de build), que copia o `.jar` gerado no primeiro estágio e roda com `java -jar`.

Por que separar assim? Porque a imagem de build carrega o Maven, os plugins, o cache de dependências — tudo isso é peso morto em produção. Só o `.jar` final precisa ir para o ambiente que vai rodar a aplicação de verdade. Resultado: imagem final bem menor.

### O Docker Compose

O `docker-compose.yml` precisa subir dois serviços juntos:

1. O **banco de dados relacional** — MySQL ou PostgreSQL, precisa ser relacional mesmo, isso não é opcional.
2. A **aplicação**, construída a partir do Dockerfile que vocês acabaram de fazer, conversando com o banco pela rede interna que o próprio Compose cria.

Uma dica para o relatório técnico (passo 8): documentem o passo a passo de como subir esse ambiente — variáveis de ambiente necessárias, o comando `docker compose up`, quais portas ficam expostas. Quem for avaliar deve conseguir rodar o projeto de vocês só seguindo essas instruções.

---

## Passo 6 — Documentando com Swagger

Para cada rota da API, incluam na documentação Swagger/OpenAPI **pelo menos um exemplo de sucesso e um exemplo de falha**. Por exemplo:

- No cadastro de usuário: um exemplo que dá certo, e um com e-mail duplicado.
- Na troca de senha: um exemplo certo, e um em que `novaSenha` e `confirmacaoSenha` não batem.
- No login: um com credenciais válidas, e um com credenciais erradas.

Reparem que isso conecta direto com o passo 4: os exemplos de erro no Swagger só vão fazer sentido — e só vão ficar consistentes entre si — se o ProblemDetail estiver de fato implementado nas respostas. Então pensem nessa documentação como o teste final de que o tratamento de erro está funcionando igual em toda a API.

---

## Passo 7 — Montando a collection do Postman

Criem os cenários de teste no Postman e exportem como JSON. Anexem esse arquivo na raiz do projeto ou numa pasta própria, tipo `/postman`. Pensando no que já foi pedido até aqui, os cenários mínimos que essa collection precisa cobrir são:

- Cadastro de usuário válido.
- Cadastro inválido — e-mail duplicado, campo obrigatório faltando.
- Troca de senha, com sucesso e com erro.
- Atualização de dados, com sucesso e com erro.
- Busca de usuário por nome.
- Login, com sucesso e com falha.

Praticamente todos esses cenários já foram citados nos passos anteriores — a graça aqui é só reunir tudo num arquivo só, testável.

---

## Passo 8 — O relatório técnico: o único entregável de verdade

Chegamos na parte que eu mais quero que vocês prestem atenção. **O único arquivo que é submetido na plataforma da FIAP é o relatório em PDF.** Todo o código, os testes, a documentação — isso tudo fica no repositório, que é só linkado dentro do relatório. Quem avalia parte do relatório, não sai vasculhando o repositório por conta própria.

O relatório precisa conter:

1. Descrição detalhada da arquitetura da aplicação.
2. Modelagem das entidades e relacionamentos
