# Diagramas de Sequência — Sistema Financeiro Pessoal

> Um diagrama por Requisito Funcional (RF), conforme solicitado.

---

## RF01 — Cadastrar Receita

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaReceita
  participant S as SistemaFinanceiro
  participant D as ArquivoDados

  U->>T: 1: clica "Nova Receita"
  T-->>U: 2: exibe formulário
  U->>T: 3: preenche descrição, valor, data, categoria
  T->>S: 4: adicionarReceita(receita)
  S->>S: 5: valida dados
  S->>D: 6: salvarDados()
  D-->>S: 7: confirmação de gravação
  S-->>T: 8: retorna sucesso
  T-->>U: 9: exibe saldo atualizado
```

---

## RF02 — Cadastrar Despesa

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaDespesa
  participant S as SistemaFinanceiro
  participant D as ArquivoDados

  U->>T: 1: clica "Nova Despesa"
  T-->>U: 2: exibe formulário
  U->>T: 3: preenche descrição, valor, data, categoria
  T->>S: 4: adicionarDespesa(despesa)
  S->>S: 5: valida dados
  S->>D: 6: salvarDados()
  D-->>S: 7: confirmação de gravação
  S-->>T: 8: retorna sucesso
  T-->>U: 9: exibe saldo atualizado
```

---

## RF03 — Gerenciar Categorias

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaCategorias
  participant S as SistemaFinanceiro
  participant D as ArquivoDados

  U->>T: 1: clica "Categorias"
  T->>S: 2: listarCategorias()
  S-->>T: 3: lista de categorias
  T-->>U: 4: exibe categorias cadastradas
  U->>T: 5: adiciona / edita / exclui categoria
  T->>S: 6: atualizarCategoria(categoria)
  S->>D: 7: salvarDados()
  D-->>S: 8: confirmação de gravação
  S-->>T: 9: lista atualizada
  T-->>U: 10: exibe lista atualizada
```

---

## RF04 — Calcular Saldo Automático

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaPrincipal
  participant S as SistemaFinanceiro

  U->>T: 1: abre o sistema
  T->>S: 2: calcularSaldo()
  S->>S: 3: soma todas as receitas
  S->>S: 4: soma todas as despesas
  S->>S: 5: saldo = totalReceitas - totalDespesas
  S-->>T: 6: retorna saldo, totalReceitas, totalDespesas
  T-->>U: 7: exibe Saldo Atual, Total Receitas, Total Despesas
```

---

## RF05 — Filtrar por Período

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaPrincipal
  participant R as RelatorioFinanceiro
  participant S as SistemaFinanceiro

  U->>T: 1: clica "Filtrar por Período"
  T-->>U: 2: exibe campos data início e data fim
  U->>T: 3: informa datas
  T->>R: 4: filtrarPorPeriodo(dataInicio, dataFim)
  R->>S: 5: buscarTransacoes(dataInicio, dataFim)
  S-->>R: 6: lista de transações filtradas
  R->>R: 7: calcularTotais()
  R-->>T: 8: resultado filtrado com totais
  T-->>U: 9: exibe movimentações do período selecionado
```

---

## RF06 — Gerar Relatório Financeiro

```mermaid
sequenceDiagram
  actor U as Usuário
  participant T as TelaRelatorio
  participant R as RelatorioFinanceiro
  participant S as SistemaFinanceiro
  participant D as ArquivoDados

  U->>T: 1: clica "Relatórios"
  T-->>U: 2: exibe opções de período e categoria
  U->>T: 3: define parâmetros do relatório
  T->>R: 4: gerarRelatorio()
  R->>S: 5: buscarTransacoes()
  S->>D: 6: carregarDados()
  D-->>S: 7: dados carregados
  S-->>R: 8: lista de transações
  R->>R: 9: calcularTotais()
  R-->>T: 10: relatório completo
  T-->>U: 11: exibe tabela com data, descrição e valor
```

---

## RF07 — Armazenar Dados Localmente

```mermaid
sequenceDiagram
  actor U as Usuário
  participant S as SistemaFinanceiro
  participant D as ArquivoDados

  U->>S: 1: realiza qualquer operação (receita, despesa, categoria)
  S->>S: 2: processa operação
  S->>D: 3: salvarDados()
  D-->>S: 4: confirmação de gravação local
  S-->>U: 5: operação concluída com sucesso
  Note over D: Dados armazenados localmente na máquina do usuário (100% offline)
```
