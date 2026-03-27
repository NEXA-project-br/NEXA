# Diagramas de Casos de Uso — Sistema Financeiro Pessoal (NEXA)

> Um diagrama por Requisito Funcional (RF), conforme solicitado.

---

## RF01 — Cadastrar Receita

```mermaid
graph LR
  U((Usuário))
  RF01([RF01 - Cadastrar Receita])
  U --> RF01
```

---

## RF02 — Cadastrar Despesa

```mermaid
graph LR
  U((Usuário))
  RF02([RF02 - Cadastrar Despesa])
  U --> RF02
```

---

## RF03 — Gerenciar Categorias

> Engloba criar, listar, editar e excluir categorias.

```mermaid
graph LR
  U((Usuário))
  RF03([RF03 - Gerenciar Categorias])
  U --> RF03
```

---

## RF04 — Calcular Saldo Automático

> `«include»` RF01 e RF02 — o saldo depende das receitas e despesas cadastradas.

```mermaid
graph LR
  U((Usuário))
  RF04([RF04 - Calcular Saldo Automático])
  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])

  U --> RF04
  RF04 -.->|«include»| RF01
  RF04 -.->|«include»| RF02
```

---

## RF05 — Filtrar Transações por Período

```mermaid
graph LR
  U((Usuário))
  RF05([RF05 - Filtrar por Período])
  U --> RF05
```

---

## RF06 — Gerar Relatório Financeiro

> `«include»` RF05 — o relatório utiliza o filtro de período.

```mermaid
graph LR
  U((Usuário))
  RF06([RF06 - Gerar Relatório Financeiro])
  RF05([RF05 - Filtrar por Período])

  U --> RF06
  RF06 -.->|«include»| RF05
```

---

## RF07 — Armazenar Dados Localmente

> `«include»` RF01 e RF02 — o armazenamento ocorre a cada operação de escrita.

```mermaid
graph LR
  U((Usuário))
  RF07([RF07 - Armazenar Dados Localmente])
  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])
  RF03([RF03 - Gerenciar Categorias])

  U --> RF07
  RF07 -.->|«include»| RF01
  RF07 -.->|«include»| RF02
  RF07 -.->|«include»| RF03
```

---

## Visão Geral — Todos os Casos de Uso

```mermaid
graph LR
  U((Usuário))

  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])
  RF03([RF03 - Gerenciar Categorias])
  RF04([RF04 - Calcular Saldo Automático])
  RF05([RF05 - Filtrar por Período])
  RF06([RF06 - Gerar Relatório Financeiro])
  RF07([RF07 - Armazenar Dados Localmente])

  U --> RF01
  U --> RF02
  U --> RF03
  U --> RF04
  U --> RF05
  U --> RF06
  U --> RF07

  RF04 -.->|«include»| RF01
  RF04 -.->|«include»| RF02
  RF06 -.->|«include»| RF05
  RF07 -.->|«include»| RF01
  RF07 -.->|«include»| RF02
  RF07 -.->|«include»| RF03
```
