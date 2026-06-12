# Manual do Usuario - NEXA

![Modelo atual da tela principal do NEXA](public/docs/modelo-software-painel-anotado.png)

## 1. Visao geral

O NEXA e um sistema financeiro pessoal desktop feito em Java. Ele funciona offline e ajuda o usuario a controlar receitas, despesas, categorias, saldo, relatorios, graficos e simulacoes financeiras.

O sistema guarda os dados localmente no computador do usuario, por isso nao depende de internet para cadastrar ou consultar informacoes.

### Como abrir o sistema

1. Abra o projeto no NetBeans ou em outra IDE Java.
2. Execute a classe `com.sistema.main.Main`.
3. Aguarde a tela principal abrir com o nome `Sistema Financeiro Pessoal`.

Tambem e possivel abrir pelo terminal:

1. Entre na pasta `java-project`.
2. Execute `mvn exec:java`.

### Dados de demonstracao

Para preencher o sistema com dados ficticios de teste, execute a classe `com.sistema.util.DemoDataSeeder`. Ela cria categorias, receitas e despesas de exemplo quando o banco `financeiro.db` ainda nao possui transacoes.

## 2. Tela Principal - Painel Financeiro

Esta e a primeira tela do sistema. Ela mostra o resumo financeiro e a lista de movimentacoes cadastradas.

![Tela principal com painel financeiro e indicacoes](public/docs/modelo-software-painel-anotado.png)

### O que aparece na tela

- Card `Saldo Atual`: mostra receitas menos despesas.
- Card `Total Receitas`: soma todas as receitas filtradas.
- Card `Total Despesas`: soma todas as despesas filtradas.
- Tabela `Movimentacoes`: mostra data, descricao, categoria, tipo e valor.
- Campo de busca: pesquisa por data, descricao, categoria, tipo ou valor.
- Filtro `Periodo`: filtra por todas as movimentacoes, ultimos dias, mes especifico ou ano especifico.
- Botoes principais: `Graficos`, `Calculadoras`, `Relatorios`, `Categorias` e `Novo`.

### Indicacao das areas da tela

- Parte superior esquerda: titulo `Painel Financeiro`, indicando que o usuario esta na tela principal.
- Parte superior direita: botoes de acesso rapido para `Graficos`, `Calculadoras`, `Relatorios`, `Categorias` e `Novo`.
- Faixa central: cards de resumo com `Saldo Atual`, `Total Receitas` e `Total Despesas`.
- Area inferior: tabela `Movimentacoes`, onde aparecem os lancamentos cadastrados.
- Campo `Buscar`: usado para localizar movimentacoes rapidamente.
- Filtro `Periodo`: usado para mostrar somente os registros do periodo escolhido.

### Como usar o painel

1. Abra o sistema.
2. Confira os cards de saldo, receitas e despesas.
3. Veja as movimentacoes na tabela.
4. Use a busca ou o filtro de periodo para encontrar registros especificos.
5. De duplo clique em uma movimentacao para editar ou excluir.

## 3. Menu Novo

O botao `Novo` abre as opcoes de cadastro de transacao.

### Opcoes do menu

- `Nova Receita`: abre a tela para cadastrar entrada de dinheiro.
- `Nova Despesa`: abre a tela para cadastrar saida de dinheiro.

### Como usar

1. Na tela principal, clique em `Novo`.
2. Escolha `Nova Receita` ou `Nova Despesa`.
3. Preencha os dados da transacao.
4. Clique em `Salvar`.

## 4. Tela Nova Receita

Esta tela serve para cadastrar uma receita, como salario, pagamento de cliente, venda ou qualquer entrada de dinheiro.

### Campos da tela

- `Tipo`: fica como `Receita`.
- `Descricao`: nome ou explicacao da receita.
- `Valor (R$)`: valor recebido.
- `Data`: data da receita no formato `dd/MM/yyyy`.
- `Categoria`: categoria relacionada a receita.

### Como cadastrar uma receita

1. Clique em `Novo`.
2. Escolha `Nova Receita`.
3. Digite a descricao da receita.
4. Informe o valor.
5. Informe a data ou use o botao de calendario.
6. Escolha uma categoria de receita, se houver.
7. Clique em `Salvar`.
8. Confira a mensagem de sucesso.

## 5. Tela Nova Despesa

Esta tela serve para cadastrar uma despesa, como mercado, conta de luz, transporte, aluguel ou qualquer saida de dinheiro.

### Campos da tela

- `Tipo`: fica como `Despesa`.
- `Descricao`: nome ou explicacao da despesa.
- `Valor (R$)`: valor gasto.
- `Data`: data da despesa no formato `dd/MM/yyyy`.
- `Categoria`: categoria relacionada a despesa.

### Como cadastrar uma despesa

1. Clique em `Novo`.
2. Escolha `Nova Despesa`.
3. Digite a descricao da despesa.
4. Informe o valor.
5. Informe a data ou use o botao de calendario.
6. Escolha uma categoria de despesa, se houver.
7. Clique em `Salvar`.
8. Confira a mensagem de sucesso.

## 6. Tela Editar Transacao

Esta tela abre quando o usuario da duplo clique em uma movimentacao da tabela principal.

### O que pode ser feito

- Alterar tipo, descricao, valor, data e categoria.
- Atualizar uma receita ou despesa ja cadastrada.
- Excluir uma transacao existente.

### Como editar uma transacao

1. Na tela principal, encontre a movimentacao desejada.
2. De duplo clique na linha da tabela.
3. Altere os campos necessarios.
4. Clique em `Atualizar`.
5. Confira a mensagem de sucesso.

### Como excluir uma transacao

1. Abra a transacao com duplo clique.
2. Clique em `Excluir`.
3. Confirme a exclusao.
4. Confira a mensagem de sucesso.

## 7. Seletor de Data

Algumas telas possuem um pequeno botao ao lado do campo de data. Ele abre um calendario para facilitar a escolha da data.

### Como usar

1. Clique no botao de calendario ao lado do campo `Data`.
2. Use os botoes de navegacao para mudar de mes, se necessario.
3. Clique no dia desejado.
4. A data sera preenchida automaticamente no campo.

## 8. Tela Categorias

Esta tela serve para cadastrar e excluir categorias usadas nas receitas e despesas.

### O que aparece na tela

- Campo para digitar o nome da categoria.
- Seletor de tipo: `Receita` ou `Despesa`.
- Lista de categorias cadastradas.
- Botao `Adicionar`.
- Botao `Excluir Selecionada`.
- Botao `Fechar`.

### Como cadastrar uma categoria

1. Na tela principal, clique em `Categorias`.
2. Escolha o tipo da categoria: `Receita` ou `Despesa`.
3. Digite o nome da categoria.
4. Clique em `Adicionar`.
5. Confira a mensagem de sucesso.

### Como excluir uma categoria

1. Abra a tela `Categorias`.
2. Escolha o tipo para listar as categorias.
3. Selecione a categoria desejada.
4. Clique em `Excluir Selecionada`.
5. Confirme a exclusao.

Se a categoria estiver vinculada a alguma transacao, o sistema exibira um aviso e nao fara a exclusao.

## 9. Menu Graficos

O botao `Graficos` abre opcoes para visualizar graficos mensais.

### Opcoes do menu

- `Despesa Mensal`: mostra as despesas por mes.
- `Receita Mensal`: mostra as receitas por mes.

### Como abrir um grafico

1. Na tela principal, clique em `Graficos`.
2. Escolha `Despesa Mensal` ou `Receita Mensal`.
3. Analise o grafico exibido.
4. Clique em `Fechar` para voltar ao painel.

## 10. Tela Grafico de Despesa Mensal

Esta tela mostra a soma das despesas por mes.

### Como usar

1. Abra o menu `Graficos`.
2. Clique em `Despesa Mensal`.
3. Observe os meses e os valores apresentados no grafico.
4. Use o resultado para identificar em quais meses houve mais gastos.

## 11. Tela Grafico de Receita Mensal

Esta tela mostra a soma das receitas por mes.

### Como usar

1. Abra o menu `Graficos`.
2. Clique em `Receita Mensal`.
3. Observe os meses e os valores apresentados no grafico.
4. Use o resultado para comparar a evolucao das entradas de dinheiro.

## 12. Tela Relatorios

Esta tela gera um relatorio financeiro por periodo.

### O que aparece na tela

- Campo `Data inicial`.
- Campo `Data final`.
- Botao `Filtrar`.
- Resumo com total de receitas, total de despesas e saldo.
- Tabela com as transacoes encontradas.
- Botao `Exportar para PDF`.
- Botao `Fechar`.

### Como consultar um relatorio

1. Na tela principal, clique em `Relatorios`.
2. Informe a data inicial.
3. Informe a data final.
4. Clique em `Filtrar`.
5. Confira os totais e a lista de transacoes.

### Como exportar para PDF

1. Gere um relatorio usando um periodo.
2. Clique em `Exportar para PDF`.
3. Escolha o local onde o arquivo sera salvo.
4. Digite o nome do arquivo.
5. Confirme a exportacao.

Se ja existir um arquivo com o mesmo nome, o sistema perguntara se deseja substituir.

## 13. Menu Calculadoras

O botao `Calculadoras` abre uma tela com as simulacoes financeiras disponiveis.

### Calculadoras disponiveis

- `Calculadora de Juros Compostos`.
- `Calculadora de Renda`.
- `Primeiro Milhao`.

### Como abrir uma calculadora

1. Na tela principal, clique em `Calculadoras`.
2. Escolha a calculadora desejada.
3. Clique em `Abrir`.
4. Preencha os campos.
5. Clique em `Calcular`.

## 14. Calculadora de Juros Compostos

Esta tela simula o crescimento de um investimento com valor inicial, aporte mensal, taxa de juros e periodo.

### Campos da tela

- `Valor inicial (R$)`: dinheiro investido no comeco.
- `Aporte mensal (R$)`: valor aplicado todo mes.
- `Taxa de juros mensal (%)`: rendimento mensal esperado.
- `Periodo`: quantidade de meses ou anos.
- `Montante final`: resultado final da simulacao.
- `Juros ganhos`: diferenca entre o total investido e o montante final.
- Grafico de evolucao do investimento.

### Como usar

1. Abra `Calculadoras`.
2. Escolha `Calculadora de Juros Compostos`.
3. Informe o valor inicial.
4. Informe o aporte mensal.
5. Informe a taxa de juros mensal.
6. Informe o periodo.
7. Escolha se o periodo esta em `Meses` ou `Anos`.
8. Clique em `Calcular`.
9. Confira o montante final, os juros ganhos e o grafico.
10. Clique em `Limpar` para apagar os campos ou em `Fechar` para sair.

## 15. Calculadora de Renda

Esta tela mostra por quanto tempo um patrimonio pode sustentar retiradas mensais.

### Campos da tela

- `Capital inicial (R$)`: valor total disponivel.
- `Taxa de juros mensal (%)`: rendimento mensal esperado.
- `Retirada mensal (R$)`: valor que sera retirado todo mes.
- `Tempo ate acabar`: tempo estimado para o dinheiro acabar.
- `Saldo final`: saldo restante no fim da simulacao.
- Grafico de evolucao do saldo.

### Como usar

1. Abra `Calculadoras`.
2. Escolha `Calculadora de Renda`.
3. Informe o capital inicial.
4. Informe a taxa de juros mensal.
5. Informe a retirada mensal.
6. Clique em `Calcular`.
7. Confira o tempo estimado e o grafico.
8. Clique em `Limpar` para apagar os campos ou em `Fechar` para sair.

Se o rendimento mensal for suficiente para cobrir a retirada, o sistema informara que o patrimonio nunca se esgota.

## 16. Calculadora Primeiro Milhao

Esta tela calcula quanto tempo falta para atingir R$ 1.000.000,00.

### Campos da tela

- `Capital inicial (R$)`: valor ja acumulado.
- `Aporte mensal (R$)`: valor que sera investido todo mes.
- `Taxa de juros mensal (%)`: rendimento mensal esperado.
- `Tempo necessario`: tempo estimado para chegar a R$ 1.000.000,00.
- `Patrimonio final`: patrimonio projetado.
- Grafico de evolucao do patrimonio.

### Como usar

1. Abra `Calculadoras`.
2. Escolha `Primeiro Milhao`.
3. Informe o capital inicial.
4. Informe o aporte mensal.
5. Informe a taxa de juros mensal.
6. Clique em `Calcular`.
7. Confira o tempo necessario e o patrimonio final.
8. Clique em `Limpar` para apagar os campos ou em `Fechar` para sair.

## 17. Busca e Filtros da Tela Principal

### Como buscar movimentacoes

1. Na tela principal, clique no campo de busca acima da tabela.
2. Digite uma data, descricao, categoria, tipo ou valor.
3. A tabela sera atualizada automaticamente.

### Como filtrar por periodo

1. Na tela principal, localize o filtro `Periodo`.
2. Escolha uma das opcoes:
   - `Todas`.
   - `Ultimos 30 dias`.
   - `Ultimos 60 dias`.
   - `Ultimos 90 dias`.
   - `Mes especifico`.
   - `Ano especifico`.
3. Se escolher `Mes especifico`, selecione o mes e o ano.
4. Se escolher `Ano especifico`, selecione o ano.
5. Confira a tabela e os cards atualizados.

## 18. Validacoes e Mensagens

O sistema mostra avisos quando alguma informacao esta incorreta ou incompleta.

### Principais validacoes

- Campos obrigatorios nao podem ficar vazios.
- Valores devem ser validos e maiores que zero quando necessario.
- Datas devem seguir o formato `dd/MM/yyyy`.
- A data inicial do relatorio nao pode ser maior que a data final.
- Categorias duplicadas nao sao aceitas.
- Categorias vinculadas a transacoes nao podem ser excluidas.
- Transacoes excluidas pedem confirmacao antes da remocao.

## 19. Como Sair do Sistema

1. Clique no `X` da janela principal.
2. Quando aparecer a confirmacao, clique em `Sim`.
3. O sistema sera fechado.

## 20. Dicas de Uso

- Cadastre as categorias antes de cadastrar receitas e despesas.
- Use nomes claros nas descricoes, como `Salario`, `Mercado`, `Conta de luz` ou `Freelance`.
- Consulte os relatorios para conferir periodos especificos.
- Use os graficos para comparar receitas e despesas ao longo dos meses.
- Use as calculadoras para simular investimentos, retiradas e metas financeiras.
