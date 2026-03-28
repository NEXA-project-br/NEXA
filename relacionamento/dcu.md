# Diagramas de Casos de Uso — Sistema Financeiro Pessoal (NEXA)

> Um diagrama por Requisito Funcional (RF), conforme solicitado.

---

## RF01 — Cadastrar Receita

> O usuário registra uma nova receita no sistema.

```mermaid
graph LR
  U((Usuário))
  RF01([Cadastrar Receita])

  U --> RF01
```

---

## RF02 — Cadastrar Despesa

> O usuário registra uma nova despesa no sistema.

```mermaid
graph LR
  U((Usuário))
  RF02([Cadastrar Despesa])

  U --> RF02
```

---

## RF03 — Gerenciar Categorias

> O usuário pode criar, editar, excluir e visualizar categorias.

```mermaid
graph LR
  U((Usuário))
  RF03([Gerenciar Categorias])

  A1([Criar Categoria])
  A2([Editar Categoria])
  A3([Excluir Categoria])
  A4([Listar Categorias])

  U --> RF03

  RF03 -.->|include| A1
  RF03 -.->|include| A2
  RF03 -.->|include| A3
  RF03 -.->|include| A4
```

---

## RF04 — Visualizar Saldo

> O usuário visualiza o saldo calculado automaticamente com base nas receitas e despesas cadastradas.

```mermaid
graph LR
  U((Usuário))
  RF04([Visualizar Saldo])

  U --> RF04
```

---

## RF05 — Filtrar Transações por Período

> O usuário filtra as transações informando um intervalo de datas.

```mermaid
graph LR
  U((Usuário))
  RF05([Filtrar Transações por Período])

  U --> RF05
```

---

## RF06 — Gerar Relatório Financeiro

> O usuário gera um relatório financeiro com base em um período.

```mermaid
graph LR
  U((Usuário))
  RF06([Gerar Relatório Financeiro])
  RF05([Filtrar Transações por Período])

  U --> RF06
  RF06 -.->|include| RF05
```

---

## Visão Geral — Todos os Casos de Uso

```mermaid
graph LR
  U((Usuário))

  RF01([Cadastrar Receita])
  RF02([Cadastrar Despesa])
  RF03([Gerenciar Categorias])
  A1([Criar Categoria])
  A2([Editar Categoria])
  A3([Excluir Categoria])
  A4([Listar Categorias])
  RF04([Visualizar Saldo])
  RF05([Filtrar Transações por Período])
  RF06([Gerar Relatório Financeiro])

  U --> RF01
  U --> RF02
  U --> RF03
  U --> RF04
  U --> RF05
  U --> RF06

  RF03 -.->|include| A1
  RF03 -.->|include| A2
  RF03 -.->|include| A3
  RF03 -.->|include| A4

  RF06 -.->|include| RF05
```
