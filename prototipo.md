# Protótipo - NEXA

## Objetivo

O protótipo do NEXA é uma aplicação desktop em Java Swing para controle financeiro pessoal. Ele permite testar o fluxo principal do sistema: cadastrar categorias, registrar receitas e despesas, acompanhar saldo, consultar gráficos, gerar relatórios e usar calculadoras financeiras.

## Telas do protótipo

### Painel Financeiro

Tela inicial do sistema. Apresenta saldo atual, total de receitas, total de despesas e tabela com as últimas movimentações. A partir dela o usuário acessa novas transações, categorias, relatórios, gráficos e calculadoras.

### Nova Receita / Nova Despesa

Formulário para cadastro de movimentações. O usuário informa tipo, descrição, valor, data e categoria. As categorias são filtradas conforme o tipo selecionado.

### Editar Transação

Formulário aberto ao dar duplo clique em uma movimentação da tabela principal. Permite atualizar dados ou excluir a transação.

### Categorias

Tela de gerenciamento de categorias. Permite criar categorias de receita ou despesa, listar categorias cadastradas e excluir uma categoria selecionada.

### Gráficos

Tela de visualização mensal para receitas ou despesas. Ajuda o usuário a entender a evolução financeira por período.

### Relatórios

Tela de consulta por período. Resume receitas, despesas, saldo e transações filtradas, com opção de exportação em PDF.

### Calculadoras

Menu com calculadoras financeiras auxiliares:

- Juros compostos.
- Primeiro milhão.
- Renda.

## Como executar o protótipo

1. Abra o projeto `java-project` no NetBeans.
2. Execute a classe `com.sistema.Main`.
3. Use a tela principal para navegar pelas funcionalidades.

Também é possível executar pelo terminal, dentro da pasta `java-project`, usando:

```bash
mvn exec:java
```
