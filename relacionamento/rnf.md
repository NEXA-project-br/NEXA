# Requisitos Não-Funcionais — Sistema Financeiro Pessoal (NEXA)

---

## RNF01 — Linguagem de Programação

- **Descrição:** O sistema deve ser desenvolvido integralmente na linguagem Java.
- **Detalhes do recurso/tecnologia:** Java 21 (LTS) — distribuição OpenJDK ou equivalente (ex.: Eclipse Temurin). Licença: GNU GPL v2 with Classpath Exception (OpenJDK). Versão mínima exigida pelo projeto: Java 17 (uso de records, sealed classes e pattern matching).

---

## RNF02 — Persistência de Dados com JPA/Hibernate

- **Descrição:** O sistema deve utilizar JPA (Jakarta Persistence API) com Hibernate como implementação ORM para gerenciar a persistência de dados.
- **Detalhes do recurso/tecnologia:**
  - **Hibernate ORM:** versão 6.x (jakarta.persistence). Licença: LGPL v2.1.
  - **Jakarta Persistence API:** versão 3.1. Licença: Eclipse Public License 2.0.
  - As entidades `Transacao` e `Categoria` são mapeadas com anotações JPA (`@Entity`, `@Table`, `@ManyToOne`, `@OneToMany`).

---

## RNF03 — Banco de Dados SQLite

- **Descrição:** O sistema deve utilizar SQLite como banco de dados local, dispensando qualquer servidor de banco de dados externo.
- **Detalhes do recurso/tecnologia:**
  - **SQLite JDBC Driver (xerial):** versão 3.x. Licença: Apache License 2.0.
  - Banco de dados armazenado no arquivo `financeiro.db` na raiz do projeto.
  - O Hibernate é configurado com `hibernate.dialect = org.hibernate.community.dialect.SQLiteDialect` e `hbm2ddl.auto = validate`.
  - A criação e migração do schema são realizadas manualmente antes da inicialização do Hibernate, garantindo que o banco esteja compatível com as entidades.

---

## RNF04 — Interface Gráfica com Java Swing

- **Descrição:** A interface do usuário deve ser construída utilizando Java Swing, garantindo portabilidade entre sistemas operacionais (Windows, Linux, macOS).
- **Detalhes do recurso/tecnologia:**
  - **Java Swing:** biblioteca padrão do JDK, sem dependência externa. Licença: incluída no JDK (Oracle/OpenJDK).
  - O Look and Feel do sistema operacional é aplicado via `UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())`.
  - A thread de interface gráfica (EDT — Event Dispatch Thread) é respeitada via `SwingUtilities.invokeLater()`.

---

## RNF05 — Gerenciamento de Dependências com Maven

- **Descrição:** O projeto deve utilizar Apache Maven para gerenciamento de dependências, compilação e empacotamento.
- **Detalhes do recurso/tecnologia:**
  - **Apache Maven:** versão 3.8+. Licença: Apache License 2.0.
  - O arquivo `pom.xml` declara todas as dependências (Hibernate, SQLite JDBC, Jakarta Persistence).
  - O projeto é empacotado como JAR executável via `maven-shade-plugin`.

---

## RNF06 — Operação Offline (100% Local)

- **Descrição:** O sistema deve operar completamente offline, sem necessidade de acesso à internet ou a servidores externos em nenhuma de suas funcionalidades.
- **Detalhes do recurso/tecnologia:** Toda persistência é feita localmente via SQLite (`financeiro.db`). Não há chamadas HTTP, APIs externas ou serviços de nuvem integrados ao sistema.

---

## RNF07 — Compatibilidade de Schema e Migrações

- **Descrição:** O sistema deve garantir compatibilidade retroativa do esquema do banco de dados, executando migrações automáticas ao inicializar, sem perda de dados existentes.
- **Detalhes do recurso/tecnologia:**
  - Migrações executadas diretamente via JDBC (classe `Main.executarMigracoes()`) antes da inicialização do Hibernate.
  - Estratégia: verificação idempotente via `PRAGMA table_info` do SQLite antes de aplicar qualquer `ALTER TABLE`.
  - Necessário pois o SQLite possui limitações para alterações de schema em tabelas existentes. O Hibernate apenas valida o schema final com `hbm2ddl.auto = validate`.

---

## RNF08 — Validação de Dados nas Camadas de Negócio

- **Descrição:** O sistema deve validar todos os dados de entrada antes de persistir, rejeitando valores inválidos com mensagens de erro descritivas.
- **Detalhes do recurso/tecnologia:**
  - Validações implementadas nos controllers (`TransacaoController`, `CategoriaController`) via exceções `IllegalArgumentException` e `IllegalStateException`.
  - Exemplos: valor deve ser maior que zero, descrição não pode ser vazia, categoria não pode ser excluída se possuir transações vinculadas, nome de categoria não pode ser duplicado.
