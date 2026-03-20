# Diagramas de Casos de Uso — Sistema Financeiro Pessoal

> Um diagrama por Requisito Funcional (RF), conforme solicitado.

---

## RF01 — Cadastrar Receita

```mermaid
graph LR
  U((Usuário))
  RF01([RF01 - Cadastrar receita])
  U --> RF01
```

---

## RF02 — Cadastrar Despesa

```mermaid
graph LR
  U((Usuário))
  RF02([RF02 - Cadastrar despesa])
  U --> RF02
```

---

## RF03 — Gerenciar Categorias

```mermaid
graph LR
  U((Usuário))
  RF03([RF03 - Gerenciar categorias])
  U --> RF03
```

---

## RF04 — Calcular Saldo Automático

> `«include»` RF01 e RF02 — o cálculo depende de receitas e despesas cadastradas.

```mermaid
graph LR
  U((Usuário))
  RF04([RF04 - Calcular saldo automático])
  RF01([RF01 - Cadastrar receita])
  RF02([RF02 - Cadastrar despesa])

  U --> RF04
  RF04 -.->|«include»| RF01
  RF04 -.->|«include»| RF02
```

---

## RF05 — Filtrar por Período

```mermaid
graph LR
  U((Usuário))
  RF05([RF05 - Filtrar por período])
  U --> RF05
```

---

## RF06 — Gerar Relatório Financeiro

> `«include»` RF05 — o relatório utiliza o filtro de período.

```mermaid
graph LR
  U((Usuário))
  RF06([RF06 - Gerar relatório financeiro])
  RF05([RF05 - Filtrar por período])

  U --> RF06
  RF06 -.->|«include»| RF05
```

---

## RF07 — Armazenar Dados Localmente

```mermaid
graph LR
  U((Usuário))
  RF07([RF07 - Armazenar dados localmente])
  U --> RF07
```

---

## Visão Geral — Todos os Casos de Uso

```mermaid
graph LR
  U((Usuário))

  RF01([RF01 - Cadastrar receita])
  RF02([RF02 - Cadastrar despesa])
  RF03([RF03 - Gerenciar categorias])
  RF04([RF04 - Calcular saldo automático])
  RF05([RF05 - Filtrar por período])
  RF06([RF06 - Gerar relatório financeiro])
  RF07([RF07 - Armazenar dados localmente])

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
```
