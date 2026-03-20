Projeto IFSP para saber mais do projeto só clicar nos links abaixo:


[Projeto](https://github.com/conradobr1/ProjetoIFSP/blob/main/Projeto.md)<br>  

[Contrato](https://github.com/conradobr1/ProjetoIFSP/blob/main/Contrato.md)<br>  

[Equipe](https://github.com/conradobr1/ProjetoIFSP/blob/main/Equipe.md)<br>

[Orçamento](https://github.com/conradobr1/ProjetoIFSP/blob/main/Orcamento.md)<br>



---

# dc.md
```md
# Diagrama de Classes – Sistema Nexa

```mermaid
classDiagram

class Usuario {
id
nome
email
senha
}

class Categoria {
id
nome
tipo
}

class Transacao {
id
descricao
valor
data
tipo
}

Usuario "1" --> "many" Transacao
Categoria "1" --> "many" Transacao
