# Diagramas de Casos de Uso — Sistema Financeiro Pessoal (NEXA)

> Um diagrama por Requisito Funcional (RF), conforme solicitado.

---

## RF01 — Cadastrar Receita

> O usuário preenche os dados da receita (descrição, valor, data, categoria) e confirma o cadastro. O sistema valida e persiste a transação.

```mermaid
graph LR
  U((Usuario))
  RF01([RF01 - Cadastrar Receita])
  A1([Preencher Descricao])
  A2([Informar Valor])
  A3([Selecionar Data])
  A4([Selecionar Categoria])
  A5([Confirmar Cadastro])

  U --> RF01
  RF01 -.->|include| A1
  RF01 -.->|include| A2
  RF01 -.->|include| A3
  RF01 -.->|extend| A4
  RF01 -.->|include| A5
```

---

## RF02 — Cadastrar Despesa

> O usuário preenche os dados da despesa (descrição, valor, data, categoria) e confirma o cadastro. O sistema valida e persiste a transação.

```mermaid
graph LR
  U((Usuario))
  RF02([RF02 - Cadastrar Despesa])
  A1([Preencher Descricao])
  A2([Informar Valor])
  A3([Selecionar Data])
  A4([Selecionar Categoria])
  A5([Confirmar Cadastro])

  U --> RF02
  RF02 -.->|include| A1
  RF02 -.->|include| A2
  RF02 -.->|include| A3
  RF02 -.->|extend| A4
  RF02 -.->|include| A5
```

---

## RF03 — Gerenciar Categorias

> O usuário pode criar, editar, listar e excluir categorias. A exclusão só é permitida se não houver transações vinculadas.

```mermaid
graph LR
  U((Usuario))
  RF03([RF03 - Gerenciar Categorias])
  A1([Criar Categoria])
  A2([Editar Categoria])
  A3([Excluir Categoria])
  A4([Listar Categorias])
  A5([Validar Vinculo com Transacoes])

  U --> RF03
  RF03 -.->|include| A4
  RF03 -.->|extend| A1
  RF03 -.->|extend| A2
  RF03 -.->|extend| A3
  A3 -.->|include| A5
```

---

## RF04 — Calcular Saldo Automático

> O sistema calcula automaticamente o saldo (Receitas − Despesas) sempre que o dashboard é carregado ou atualizado. Depende de RF01 e RF02.

```mermaid
graph LR
  U((Usuario))
  RF04([RF04 - Calcular Saldo Automatico])
  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])
  A1([Somar Total de Receitas])
  A2([Somar Total de Despesas])
  A3([Exibir Saldo no Dashboard])

  U --> RF04
  RF04 -.->|include| RF01
  RF04 -.->|include| RF02
  RF04 -.->|include| A1
  RF04 -.->|include| A2
  RF04 -.->|include| A3
```

---

## RF05 — Filtrar Transações por Período

> O usuário informa a data de início e fim. O sistema filtra as transações e recalcula os totais do período selecionado.

```mermaid
graph LR
  U((Usuario))
  RF05([RF05 - Filtrar por Periodo])
  A1([Informar Data de Inicio])
  A2([Informar Data de Fim])
  A3([Aplicar Filtro])
  A4([Exibir Transacoes Filtradas])
  A5([Recalcular Totais do Periodo])

  U --> RF05
  RF05 -.->|include| A1
  RF05 -.->|include| A2
  RF05 -.->|include| A3
  A3 -.->|include| A4
  A3 -.->|include| A5
```

---

## RF06 — Gerar Relatório Financeiro

> O usuário define o período e aciona a geração do relatório. O sistema utiliza o filtro por período (RF05) e exibe totais e tabela de transações.

```mermaid
graph LR
  U((Usuario))
  RF06([RF06 - Gerar Relatorio Financeiro])
  RF05([RF05 - Filtrar por Periodo])
  A1([Definir Parametros do Relatorio])
  A2([Exibir Tabela de Transacoes])
  A3([Exibir Total Receitas e Despesas])
  A4([Exibir Saldo do Periodo])

  U --> RF06
  RF06 -.->|include| A1
  RF06 -.->|include| RF05
  RF06 -.->|include| A2
  RF06 -.->|include| A3
  RF06 -.->|include| A4
```

---

## RF07 — Armazenar Dados Localmente

> O sistema persiste automaticamente todos os dados (transações e categorias) no banco SQLite local, sem necessidade de conexão com internet.

```mermaid
graph LR
  U((Usuario))
  RF07([RF07 - Armazenar Dados Localmente])
  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])
  RF03([RF03 - Gerenciar Categorias])
  A1([Persistir no Banco SQLite])
  A2([Executar Migracao de Schema])
  A3([Encerrar Conexao com Seguranca])

  U --> RF07
  RF07 -.->|include| RF01
  RF07 -.->|include| RF02
  RF07 -.->|include| RF03
  RF07 -.->|include| A1
  RF07 -.->|include| A2
  RF07 -.->|extend| A3
```

---

## Visão Geral — Todos os Casos de Uso

```mermaid
graph LR
  U((Usuario))
  RF01([RF01 - Cadastrar Receita])
  RF02([RF02 - Cadastrar Despesa])
  RF03([RF03 - Gerenciar Categorias])
  RF04([RF04 - Calcular Saldo Automatico])
  RF05([RF05 - Filtrar por Periodo])
  RF06([RF06 - Gerar Relatorio Financeiro])
  RF07([RF07 - Armazenar Dados Localmente])

  U --> RF01
  U --> RF02
  U --> RF03
  U --> RF04
  U --> RF05
  U --> RF06
  U --> RF07

  RF04 -.->|include| RF01
  RF04 -.->|include| RF02
  RF06 -.->|include| RF05
  RF07 -.->|include| RF01
  RF07 -.->|include| RF02
  RF07 -.->|include| RF03
```
