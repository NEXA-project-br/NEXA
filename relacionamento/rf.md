# Requisitos Funcionais — Sistema Financeiro Pessoal (NEXA)

---

## RF01 — Cadastrar Receita

- **Descrição:** O sistema deve permitir que o usuário registre uma nova receita financeira, informando descrição, valor, data e categoria.
- **Entradas:** Descrição (String), Valor (BigDecimal > 0), Data (LocalDate), Categoria (opcional), Tipo = RECEITA.
- **Origem:** Usuário, via tela de formulário de transação (`TransactionFormView`).
- **Saída:** Transação do tipo `RECEITA` persistida no banco de dados local (SQLite via Hibernate).
- **Destino:** Banco de dados local (`financeiro.db`), tabela `transacoes`.
- **Ação:** O sistema valida os dados, instancia um objeto `Transacao` com tipo `TipoTransacao.RECEITA` e persiste via `TransacaoController.salvar()`.
- **Pré-condição:** O sistema deve estar inicializado (Hibernate ativo). O valor deve ser maior que zero. A descrição não pode estar em branco.
- **Pós-condição:** A transação é salva no banco. O dashboard é atualizado exibindo o novo saldo, total de receitas e despesas.
- **Efeitos colaterais:** O saldo exibido na tela principal (`MainView`) é recalculado automaticamente após a persistência.

---

## RF02 — Cadastrar Despesa

- **Descrição:** O sistema deve permitir que o usuário registre uma nova despesa financeira, informando descrição, valor, data e categoria.
- **Entradas:** Descrição (String), Valor (BigDecimal > 0), Data (LocalDate), Categoria (opcional), Tipo = DESPESA.
- **Origem:** Usuário, via tela de formulário de transação (`TransactionFormView`).
- **Saída:** Transação do tipo `DESPESA` persistida no banco de dados local.
- **Destino:** Banco de dados local (`financeiro.db`), tabela `transacoes`.
- **Ação:** O sistema valida os dados, instancia um objeto `Transacao` com tipo `TipoTransacao.DESPESA` e persiste via `TransacaoController.salvar()`.
- **Pré-condição:** O sistema deve estar inicializado. O valor deve ser maior que zero. A descrição não pode estar em branco.
- **Pós-condição:** A transação é salva no banco. O dashboard é atualizado exibindo o novo saldo.
- **Efeitos colaterais:** O saldo exibido na tela principal é recalculado automaticamente.

---

## RF03 — Gerenciar Categorias

- **Descrição:** O sistema deve permitir ao usuário criar, listar, editar e excluir categorias financeiras, associadas a um tipo (RECEITA ou DESPESA).
- **Entradas:** Nome da categoria (String, máx. 100 caracteres), Tipo (`TipoTransacao`).
- **Origem:** Usuário, via tela de gerenciamento de categorias (`CategoryView`).
- **Saída:** Categoria criada, atualizada ou removida do banco de dados.
- **Destino:** Banco de dados local (`financeiro.db`), tabela `categorias`.
- **Ação:** O sistema valida nome e tipo, verifica duplicidade e persiste via `CategoriaController`. A exclusão é bloqueada se houver transações vinculadas à categoria.
- **Pré-condição:** Nome não pode estar em branco, não pode ultrapassar 100 caracteres, não pode ser duplicado. Para exclusão, a categoria não pode possuir transações vinculadas.
- **Pós-condição:** A lista de categorias é atualizada. Formulários de transação refletem as categorias disponíveis.
- **Efeitos colaterais:** A exclusão de categorias vinculadas a transações é bloqueada para preservar o histórico financeiro. Nenhuma transação é alterada automaticamente.

---

## RF04 — Calcular Saldo Automático

- **Descrição:** O sistema deve calcular e exibir automaticamente o saldo atual do usuário (Total Receitas − Total Despesas), sempre que o dashboard for carregado ou atualizado.
- **Entradas:** Nenhuma entrada direta do usuário. O cálculo é realizado com base em todas as transações persistidas.
- **Origem:** Sistema, acionado na inicialização da `MainView` e após cada operação de cadastro ou exclusão de transação.
- **Saída:** Exibição dos valores de Total Receitas, Total Despesas e Saldo Atual nos cards do dashboard.
- **Destino:** Interface gráfica (`MainView` — labels `lblSaldo`, `lblTotalReceitas`, `lblTotalDespesas`).
- **Ação:** `TransacaoController.calcularSaldoAtual()` subtrai o total de despesas do total de receitas via consultas JPQL no `TransacaoDAO`.
- **Pré-condição:** O banco de dados deve estar acessível e o Hibernate inicializado.
- **Pós-condição:** Os cards do dashboard exibem os valores financeiros atualizados.
- **Efeitos colaterais:** Nenhum. Operação apenas de leitura.

---

## RF05 — Filtrar Transações por Período

- **Descrição:** O sistema deve permitir que o usuário filtre as transações exibidas no dashboard por um intervalo de datas (data início e data fim).
- **Entradas:** Data de início (LocalDate), Data de fim (LocalDate).
- **Origem:** Usuário, via campos de filtro de período na `MainView` ou na `ReportView`.
- **Saída:** Lista de transações dentro do período informado, com totais de receitas, despesas e saldo do período.
- **Destino:** Tabela de transações na interface gráfica.
- **Ação:** `TransacaoController.filtrarPorPeriodo()` consulta o `TransacaoDAO` com JPQL filtrando por intervalo de datas. A data inicial não pode ser posterior à data final.
- **Pré-condição:** As datas devem ser válidas e a data inicial não pode ser posterior à data final.
- **Pós-condição:** A tabela exibe apenas as transações do período selecionado.
- **Efeitos colaterais:** Nenhum. Operação de leitura.

---

## RF06 — Gerar Relatório Financeiro

- **Descrição:** O sistema deve gerar um relatório financeiro para um período específico, exibindo a lista de transações com descrição, data, valor e tipo, além dos totais de receitas, despesas e saldo do período.
- **Entradas:** Data de início e data de fim (inseridas pelo usuário na `ReportView`).
- **Origem:** Usuário, ao clicar em "Relatórios" na `MainView`.
- **Saída:** Relatório financeiro com tabela de transações e resumo (Total Receitas, Total Despesas, Saldo do Período).
- **Destino:** Tela `ReportView` (JDialog modal).
- **Ação:** `TransacaoController.gerarResumo()` retorna um record `ResumoFinanceiro` contendo totais e lista de transações filtradas por período.
- **Pré-condição:** O período informado deve ser válido (datas não nulas, início ≤ fim).
- **Pós-condição:** O relatório é exibido na tela. Nenhum dado é persistido.
- **Efeitos colaterais:** Nenhum. Operação apenas de leitura.

---

## RF07 — Armazenar Dados Localmente

- **Descrição:** Todas as transações e categorias devem ser armazenadas localmente no dispositivo do usuário, sem necessidade de conexão com internet ou servidor externo.
- **Entradas:** Qualquer operação de criação, atualização ou exclusão de transação ou categoria.
- **Origem:** Qualquer operação de escrita iniciada pelo usuário.
- **Saída:** Dados persistidos no arquivo `financeiro.db` (banco SQLite local).
- **Destino:** Sistema de arquivos local, arquivo `financeiro.db` na raiz do projeto.
- **Ação:** O Hibernate gerencia a persistência via JPA/SQLite. O `HibernateUtil` fornece o `EntityManagerFactory` e garante o fechamento correto ao encerrar a aplicação.
- **Pré-condição:** O arquivo `financeiro.db` deve ser acessível. O driver JDBC do SQLite deve estar disponível.
- **Pós-condição:** Os dados sobrevivem ao encerramento e reinicialização da aplicação.
- **Efeitos colaterais:** O `Main` executa migrações de schema via JDBC antes do Hibernate inicializar, garantindo compatibilidade com bancos criados em versões anteriores do sistema.
