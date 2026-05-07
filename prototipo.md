# Prototipo - NEXA

## Objetivo

O prototipo do NEXA e uma aplicacao desktop em Java Swing para controle financeiro pessoal. Ele permite testar o fluxo principal do sistema: cadastrar categorias, registrar receitas e despesas, acompanhar saldo, consultar graficos, gerar relatorios e usar calculadoras financeiras.

## Telas do prototipo

### Painel Financeiro

Tela inicial do sistema. Apresenta saldo atual, total de receitas, total de despesas e tabela com as ultimas movimentacoes. A partir dela o usuario acessa novas transacoes, categorias, relatorios, graficos e calculadoras.

### Nova Receita / Nova Despesa

Formulario para cadastro de movimentacoes. O usuario informa tipo, descricao, valor, data e categoria. As categorias sao filtradas conforme o tipo selecionado.

### Editar Transacao

Formulario aberto ao dar duplo clique em uma movimentacao da tabela principal. Permite atualizar dados ou excluir a transacao.

### Categorias

Tela de gerenciamento de categorias. Permite criar categorias de receita ou despesa, listar categorias cadastradas e excluir uma categoria selecionada.

### Graficos

Tela de visualizacao mensal para receitas ou despesas. Ajuda o usuario a entender a evolucao financeira por periodo.

### Relatorios

Tela de consulta por periodo. Resume receitas, despesas, saldo e transacoes filtradas, com opcao de exportacao em PDF.

### Calculadoras

Menu com calculadoras financeiras auxiliares:

- Juros compostos.
- Primeiro milhao.
- Renda.

## Como executar o prototipo

1. Abra o projeto `java-project` no NetBeans.
2. Execute a classe `com.sistema.Main`.
3. Use a tela principal para navegar pelas funcionalidades.

Tambem e possivel executar pelo terminal, dentro da pasta `java-project`, usando:

```bash
mvn exec:java
```
