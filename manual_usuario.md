# Manual do usuario - NEXA

## 1. Visao geral

O NEXA e um sistema financeiro pessoal para cadastrar receitas, despesas, categorias, consultar saldos, visualizar graficos, gerar relatorios e usar calculadoras financeiras.

Para abrir o sistema pelo NetBeans, execute a classe `com.sistema.Main`. Pelo terminal, entre na pasta `java-project` e execute `mvn exec:java`.

## 2. Tela principal - Painel Financeiro

### Funcionalidades

- Exibe o saldo atual.
- Exibe o total de receitas cadastradas.
- Exibe o total de despesas cadastradas.
- Lista as ultimas movimentacoes em uma tabela.
- Permite abrir as telas de nova receita, nova despesa, graficos, calculadoras, relatorios e categorias.
- Permite editar uma movimentacao com duplo clique.

### Como atualizar os dados do painel

1. Abra o sistema.
2. Clique em `Atualizar`.
3. Confira os cards de saldo, receitas e despesas e a tabela de movimentacoes.

### Como editar uma movimentacao

1. Na tabela `Ultimas Movimentacoes`, localize a receita ou despesa desejada.
2. De duplo clique na linha.
3. Altere os campos na tela de transacao.
4. Clique em `Atualizar`.

### Como sair do sistema

1. Feche a janela principal.
2. Na confirmacao, clique em `Sim`.

## 3. Tela Nova Receita / Nova Despesa / Editar Transacao

### Funcionalidades

- Cadastra receitas e despesas.
- Edita transacoes existentes.
- Exclui transacoes existentes.
- Permite informar tipo, descricao, valor, data e categoria.
- Filtra automaticamente as categorias conforme o tipo selecionado.

### Como cadastrar uma receita

1. Na tela principal, clique em `Novo`.
2. Selecione `Nova Receita`.
3. Confirme que o campo `Tipo` esta como `Receita`.
4. Preencha `Descricao`.
5. Preencha `Valor (R$)`.
6. Preencha `Data` no formato `dd/MM/yyyy`.
7. Selecione uma categoria, se desejar.
8. Clique em `Salvar`.

### Como cadastrar uma despesa

1. Na tela principal, clique em `Novo`.
2. Selecione `Nova Despesa`.
3. Confirme que o campo `Tipo` esta como `Despesa`.
4. Preencha `Descricao`.
5. Preencha `Valor (R$)`.
6. Preencha `Data` no formato `dd/MM/yyyy`.
7. Selecione uma categoria, se desejar.
8. Clique em `Salvar`.

### Como excluir uma transacao

1. Na tela principal, de duplo clique na transacao desejada.
2. Clique em `Excluir`.
3. Confirme a exclusao.

## 4. Tela Categorias

### Funcionalidades

- Cadastra categorias de receita ou despesa.
- Lista categorias cadastradas conforme o tipo selecionado.
- Exclui categorias selecionadas.
- Impede a exclusao de categorias vinculadas a transacoes.

### Como cadastrar uma categoria

1. Na tela principal, clique em `Categorias`.
2. Digite o nome da categoria no campo de texto.
3. Selecione o tipo: `Receita` ou `Despesa`.
4. Clique em `Adicionar`.

### Como excluir uma categoria

1. Na tela `Categorias`, selecione uma categoria na lista.
2. Clique em `Excluir Selecionada`.
3. Confirme a exclusao.
4. Se a categoria possuir transacoes vinculadas, o sistema exibira um aviso e nao fara a exclusao.

## 5. Tela Graficos

### Funcionalidades

- Exibe grafico mensal de despesas.
- Exibe grafico mensal de receitas.
- Ajuda a comparar a evolucao dos valores ao longo dos meses.

### Como visualizar grafico de despesas

1. Na tela principal, clique em `Graficos`.
2. Selecione `Despesa Mensal`.
3. Analise o grafico exibido na janela.

### Como visualizar grafico de receitas

1. Na tela principal, clique em `Graficos`.
2. Selecione `Receita Mensal`.
3. Analise o grafico exibido na janela.

## 6. Tela Relatorios

### Funcionalidades

- Filtra transacoes por periodo.
- Gera resumo com receitas, despesas e saldo do periodo.
- Lista transacoes encontradas.
- Permite exportar relatorio em PDF.

### Como consultar um relatorio

1. Na tela principal, clique em `Relatorios`.
2. Informe a data inicial.
3. Informe a data final.
4. Acione a busca/geracao do relatorio.
5. Confira os totais e a lista de transacoes.

### Como exportar relatorio em PDF

1. Gere um relatorio por periodo.
2. Clique na opcao de exportar PDF.
3. Escolha o local de destino, quando solicitado.
4. Confirme a exportacao.

## 7. Tela Calculadoras

### Funcionalidades

- Abre calculadoras financeiras auxiliares.
- Permite acessar calculadora de juros compostos.
- Permite acessar calculadora para atingir o primeiro milhao.
- Permite acessar calculadora de renda.

### Como abrir uma calculadora

1. Na tela principal, clique em `Calculadoras`.
2. Escolha a calculadora desejada.
3. Preencha os campos solicitados.
4. Clique no botao de calcular.
5. Confira o resultado apresentado.

## 8. Calculadora de Juros Compostos

### Funcionalidades

- Calcula crescimento de investimento com aporte inicial, aportes recorrentes, taxa e prazo.
- Mostra o resultado final e a evolucao do valor.

### Passo a passo

1. Abra `Calculadoras`.
2. Selecione a calculadora de juros compostos.
3. Informe o valor inicial.
4. Informe o aporte mensal.
5. Informe a taxa de juros.
6. Informe o periodo.
7. Clique em calcular.

## 9. Calculadora do Primeiro Milhao

### Funcionalidades

- Estima quanto tempo falta para chegar a R$ 1.000.000,00.
- Considera valores iniciais, aportes e rentabilidade.

### Passo a passo

1. Abra `Calculadoras`.
2. Selecione a calculadora do primeiro milhao.
3. Informe o valor ja acumulado.
4. Informe o aporte mensal.
5. Informe a rentabilidade esperada.
6. Clique em calcular.

## 10. Calculadora de Renda

### Funcionalidades

- Estima renda gerada por um patrimonio ou investimento.
- Usa taxa informada pelo usuario para calcular retorno aproximado.

### Passo a passo

1. Abra `Calculadoras`.
2. Selecione a calculadora de renda.
3. Informe o patrimonio ou valor investido.
4. Informe a taxa de rendimento.
5. Clique em calcular.

## 11. Validacoes e mensagens

- Campos obrigatorios vazios exibem mensagem de erro.
- Valores devem ser maiores que zero.
- Datas devem seguir o formato `dd/MM/yyyy`.
- A data inicial de um relatorio nao pode ser posterior a data final.
- Categorias duplicadas nao sao aceitas.
- Categorias com transacoes vinculadas nao podem ser excluidas.
