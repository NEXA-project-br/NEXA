# Diagrama de Classes — Sistema Financeiro Pessoal

---

## Diagrama de Classes

```mermaid
classDiagram
  class Transacao {
    -int id
    -String descricao
    -double valor
    -Date data
    -String tipo
    +getId() int
    +getDescricao() String
    +getValor() double
    +getData() Date
    +getTipo() String
  }

  class Receita {
    +Receita(descricao String, valor double, data Date, categoria Categoria)
    +calcularSaldo() double
  }

  class Despesa {
    +Despesa(descricao String, valor double, data Date, categoria Categoria)
    +calcularSaldo() double
  }

  class Categoria {
    -int id
    -String nome
    +getId() int
    +getNome() String
    +setNome(nome String) void
  }

  class RelatorioFinanceiro {
    -Date dataInicio
    -Date dataFim
    -double totalReceitas
    -double totalDespesas
    -double saldoFinal
    +gerarRelatorio() void
    +filtrarPorPeriodo(inicio Date, fim Date) List
    +calcularTotais() void
  }

  class SistemaFinanceiro {
    -List~Transacao~ transacoes
    -List~Categoria~ categorias
    +adicionarReceita(r Receita) void
    +adicionarDespesa(d Despesa) void
    +calcularSaldo() double
    +salvarDados() void
    +carregarDados() void
  }

  Transacao <|-- Receita : herança
  Transacao <|-- Despesa : herança
  Transacao --> Categoria : pertence a
  SistemaFinanceiro "1" --> "*" Transacao : gerencia
  SistemaFinanceiro "1" --> "*" Categoria : gerencia
  RelatorioFinanceiro --> SistemaFinanceiro : consulta
```

---

## Dicionário de Dados

### Classe `Transacao` (abstrata — classe pai)

| Atributo | Tipo | Descrição |
|---|---|---|
| id | int | Identificador único da transação |
| descricao | String | Descrição textual da transação |
| valor | double | Valor monetário da transação |
| data | Date | Data em que a transação ocorreu |
| tipo | String | Tipo: `"RECEITA"` ou `"DESPESA"` |

---

### Classe `Receita` (herda de `Transacao`)

| Atributo / Método | Tipo | Descrição |
|---|---|---|
| Receita(...) | Construtor | Cria uma nova receita com descrição, valor, data e categoria |
| calcularSaldo() | double | Retorna o valor positivo da receita para o cálculo do saldo |

---

### Classe `Despesa` (herda de `Transacao`)

| Atributo / Método | Tipo | Descrição |
|---|---|---|
| Despesa(...) | Construtor | Cria uma nova despesa com descrição, valor, data e categoria |
| calcularSaldo() | double | Retorna o valor negativo da despesa para o cálculo do saldo |

---

### Classe `Categoria`

| Atributo | Tipo | Descrição |
|---|---|---|
| id | int | Identificador único da categoria |
| nome | String | Nome da categoria (ex: Alimentação, Transporte, Lazer, Moradia) |

---

### Classe `SistemaFinanceiro`

| Atributo / Método | Tipo | Descrição |
|---|---|---|
| transacoes | List\<Transacao\> | Lista de todas as receitas e despesas cadastradas |
| categorias | List\<Categoria\> | Lista de categorias disponíveis |
| adicionarReceita(r) | void | Adiciona uma receita à lista e salva os dados |
| adicionarDespesa(d) | void | Adiciona uma despesa à lista e salva os dados |
| calcularSaldo() | double | Retorna `totalReceitas - totalDespesas` |
| salvarDados() | void | Persiste os dados em arquivo local |
| carregarDados() | void | Carrega os dados do arquivo local ao iniciar o sistema |

---

### Classe `RelatorioFinanceiro`

| Atributo | Tipo | Descrição |
|---|---|---|
| dataInicio | Date | Data inicial do período do relatório |
| dataFim | Date | Data final do período do relatório |
| totalReceitas | double | Soma de todas as receitas no período |
| totalDespesas | double | Soma de todas as despesas no período |
| saldoFinal | double | Resultado: `totalReceitas - totalDespesas` |
| gerarRelatorio() | void | Gera e exibe o relatório financeiro |
| filtrarPorPeriodo(...) | List | Retorna transações entre dataInicio e dataFim |
| calcularTotais() | void | Calcula e preenche totalReceitas, totalDespesas e saldoFinal |
