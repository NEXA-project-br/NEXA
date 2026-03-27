# Diagramas de Sequência — Sistema Financeiro Pessoal (NEXA)

> Um diagrama por Requisito Funcional (RF), conforme solicitado.
> Os participantes refletem as classes reais do projeto Java.

---

## RF01 — Cadastrar Receita

```mermaid
sequenceDiagram
  actor U as Usuário
  participant V as TransactionFormView
  participant C as TransacaoController
  participant D as TransacaoDAO
  participant DB as financeiro.db (SQLite)

  U->>V: 1: clica "Nova Receita" (MainView)
  V-->>U: 2: exibe formulário (descrição, valor, data, categoria, tipo=RECEITA)
  U->>V: 3: preenche campos e confirma
  V->>C: 4: salvar(transacao)
  C->>C: 5: validarTransacao(transacao)
  C->>D: 6: salvar(transacao)
  D->>DB: 7: INSERT INTO transacoes (...)
  DB-->>D: 8: confirmação de inserção
  D-->>C: 9: retorna Transacao com ID gerado
  C-->>V: 10: retorna Transacao persistida
  V-->>U: 11: fecha formulário e atualiza dashboard (atualizarDashboard)
```

---

## RF02 — Cadastrar Despesa

```mermaid
sequenceDiagram
  actor U as Usuário
  participant V as TransactionFormView
  participant C as TransacaoController
  participant D as TransacaoDAO
  participant DB as financeiro.db (SQLite)

  U->>V: 1: clica "Nova Despesa" (MainView)
  V-->>U: 2: exibe formulário (descrição, valor, data, categoria, tipo=DESPESA)
  U->>V: 3: preenche campos e confirma
  V->>C: 4: salvar(transacao)
  C->>C: 5: validarTransacao(transacao)
  C->>D: 6: salvar(transacao)
  D->>DB: 7: INSERT INTO transacoes (...)
  DB-->>D: 8: confirmação de inserção
  D-->>C: 9: retorna Transacao com ID gerado
  C-->>V: 10: retorna Transacao persistida
  V-->>U: 11: fecha formulário e atualiza dashboard (atualizarDashboard)
```

---

## RF03 — Gerenciar Categorias

```mermaid
sequenceDiagram
  actor U as Usuário
  participant V as CategoryView
  participant C as CategoriaController
  participant D as CategoriaDAO
  participant DB as financeiro.db (SQLite)

  U->>V: 1: abre tela "Categorias"
  V->>C: 2: listarTodos()
  C->>D: 3: listarOrdenadoPorNome()
  D->>DB: 4: SELECT * FROM categorias ORDER BY nome
  DB-->>D: 5: lista de categorias
  D-->>C: 6: List~Categoria~
  C-->>V: 7: lista de categorias
  V-->>U: 8: exibe categorias cadastradas

  alt Criar nova categoria
    U->>V: 9a: informa nome e tipo, clica "Salvar"
    V->>C: 10a: salvarPorNome(nome, tipo)
    C->>C: 11a: validarCategoria() + verifica duplicidade
    C->>D: 12a: salvar(categoria)
    D->>DB: 13a: INSERT INTO categorias (...)
    DB-->>D: 14a: confirmação
    D-->>C: 15a: Categoria persistida
    C-->>V: 16a: retorna Categoria com ID
  else Excluir categoria
    U->>V: 9b: seleciona categoria, clica "Excluir"
    V->>C: 10b: excluir(id)
    C->>D: 11b: possuiTransacoes(id)
    D->>DB: 12b: SELECT COUNT(*) FROM transacoes WHERE categoria_id = ?
    DB-->>D: 13b: count
    D-->>C: 14b: boolean
    alt Sem transações vinculadas
      C->>D: 15b: excluir(id)
      D->>DB: 16b: DELETE FROM categorias WHERE id = ?
      DB-->>D: 17b: confirmação
    else Com transações vinculadas
      C-->>V: 15b: lança IllegalStateException
      V-->>U: 16b: exibe mensagem de erro
    end
  end

  V-->>U: atualiza lista de categorias
```

---

## RF04 — Calcular Saldo Automático

```mermaid
sequenceDiagram
  actor U as Usuário
  participant V as MainView
  participant C as TransacaoController
  participant D as TransacaoDAO
  participant DB as financeiro.db (SQLite)

  U->>V: 1: inicia o sistema (ou retorna ao dashboard)
  V->>C: 2: calcularSaldoAtual()
  C->>D: 3: totalReceitas()
  D->>DB: 4: SELECT SUM(valor) FROM transacoes WHERE tipo = 'RECEITA'
  DB-->>D: 5: BigDecimal totalReceitas
  D-->>C: 6: totalReceitas
  C->>D: 7: totalDespesas()
  D->>DB: 8: SELECT SUM(valor) FROM transacoes WHERE tipo = 'DESPESA'
  DB-->>D: 9: BigDecimal totalDespesas
  D-->>C: 10: totalDespesas
  C->>C: 11: saldo = totalReceitas.subtract(totalDespesas)
  C-->>V: 12: retorna saldo, totalReceitas, totalDespesas
  V-->>U: 13: exibe cards "Saldo Atual", "Total Receitas", "Total Despesas"
```

---

## RF05 — Filtrar Transações por Período

```mermaid
sequenceDiagram
  actor U as Usuário
  participant V as MainView
  participant C as TransacaoController
  participant D as TransacaoDAO
  participant DB as financeiro.db (SQLite)

  U->>V: 1: informa data início e data fim, clica "Filtrar"
  V->>C: 2: filtrarPorPeriodo(inicio, fim)
  C->>C: 3: validarPeriodo(inicio, fim)
  C->>D: 4: filtrarPorPeriodo(inicio, fim)
  D->>DB: 5: SELECT * FROM transacoes WHERE data BETWEEN ? AND ? ORDER BY data DESC
  DB-->>D: 6: lista de transações
  D-->>C: 7: List~Transacao~
  C-->>V: 8: lista filtrada
  V-->>U: 9: exibe transações do período na tabela
```

---

## RF06 — Gerar Relatório Financeiro

```mermaid
sequenceDiagram
  actor U as Usuário
  participant MV as MainView
  participant RV as ReportView
  participant C as TransacaoController
  participant D as TransacaoDAO
  participant DB as financeiro.db (SQLite)

  U->>MV: 1: clica "Relatórios"
  MV->>RV: 2: abre ReportView (dialog modal)
  RV-->>U: 3: exibe campos data início e data fim (padrão: mês atual)
  U->>RV: 4: confirma período e clica "Gerar Relatório"
  RV->>C: 5: gerarResumo(inicio, fim)
  C->>C: 6: validarPeriodo(inicio, fim)
  C->>D: 7: somarPorTipoEPeriodo(RECEITA, inicio, fim)
  D->>DB: 8: SELECT SUM(valor) ... WHERE tipo='RECEITA' AND data BETWEEN ? AND ?
  DB-->>D: 9: BigDecimal totalReceitas
  C->>D: 10: somarPorTipoEPeriodo(DESPESA, inicio, fim)
  D->>DB: 11: SELECT SUM(valor) ... WHERE tipo='DESPESA' AND data BETWEEN ? AND ?
  DB-->>D: 12: BigDecimal totalDespesas
  C->>D: 13: filtrarPorPeriodo(inicio, fim)
  D->>DB: 14: SELECT * FROM transacoes WHERE data BETWEEN ? AND ?
  DB-->>D: 15: lista de transações
  D-->>C: 16: List~Transacao~
  C->>C: 17: cria ResumoFinanceiro(inicio, fim, receitas, despesas, saldo, transacoes)
  C-->>RV: 18: ResumoFinanceiro
  RV-->>U: 19: exibe tabela com transações + cards de totais (Receitas, Despesas, Saldo)
```

---

## RF07 — Armazenar Dados Localmente

```mermaid
sequenceDiagram
  actor U as Usuário
  participant Main as Main (inicialização)
  participant HU as HibernateUtil
  participant DB as financeiro.db (SQLite)

  U->>Main: 1: inicia a aplicação
  Main->>Main: 2: executarMigracoes() via JDBC direto
  Main->>DB: 3: PRAGMA table_info(categorias) — verifica schema
  DB-->>Main: 4: estrutura atual da tabela
  alt Coluna 'tipo' ausente
    Main->>DB: 5a: ALTER TABLE categorias ADD COLUMN tipo VARCHAR(10)
    Main->>DB: 6a: UPDATE categorias SET tipo = 'DESPESA' WHERE tipo IS NULL
    DB-->>Main: 7a: migrações aplicadas
  end
  Main->>HU: 8: getEntityManagerFactory()
  HU->>DB: 9: conecta via Hibernate (hbm2ddl.auto = update)
  DB-->>HU: 10: EntityManagerFactory criado
  HU-->>Main: 11: factory pronto

  Note over DB: Todas as operações subsequentes (salvar, atualizar, excluir)<br/>são persistidas automaticamente no financeiro.db via JPA/Hibernate

  U->>Main: 12: encerra a aplicação
  Main->>HU: 13: ShutdownHook → HibernateUtil.shutdown()
  HU->>DB: 14: fecha conexões e EntityManagerFactory
```
