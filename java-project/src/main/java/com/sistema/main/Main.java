package com.sistema;

import com.sistema.util.HibernateUtil;
import com.sistema.util.SQLiteBackupUtil;
import com.sistema.view.MainView;

import javax.swing.*;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Ponto de entrada da aplicacao Sistema Financeiro Pessoal.
 *
 * Responsabilidades:
 * - Executa migracoes de schema antes de inicializar o Hibernate
 * - Inicializa o EntityManagerFactory (Hibernate/SQLite)
 * - Exibe a tela principal na Event Dispatch Thread (EDT)
 * - Garante o desligamento correto do Hibernate ao fechar a JVM
 */
public class Main {

    /**
     * URL de conexao com o banco de dados SQLite.
     */
    private static final String DB_URL = "jdbc:sqlite:financeiro.db?foreign_keys=on";

    /**
     * Inicia a aplicacao e prepara o ambiente grafico.
     *
     * @param args argumentos recebidos pela linha de comando
     */
    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Sistema] Encerrando — fechando EntityManagerFactory...");
            HibernateUtil.shutdown();
        }));

        // 1. Migracoes de schema ANTES do Hibernate (compatibilidade SQLite)
        try {
            executarMigracoes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro na migracao do banco de dados:\n" + e.getMessage(),
                    "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 2. Inicializa o Hibernate (cria/atualiza tabelas restantes)
        try {
            System.out.println("[Sistema] Inicializando Hibernate...");
            HibernateUtil.getEntityManagerFactory();
            System.out.println("[Sistema] Hibernate inicializado com sucesso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao inicializar o banco de dados:\n" + e.getMessage(),
                    "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        criarBackupInicial();

        // 3. Configura Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // 4. Exibe a tela principal na EDT
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }

    /**
     * Executa migracoes de schema diretamente via JDBC, antes do Hibernate.
     *
     * Isso e necessario porque o SQLite nao suporta ADD COLUMN NOT NULL
     * em tabelas existentes — o Hibernate falharia ao tentar o ALTER TABLE.
     *
     * Cada migracao e idempotente: verifica se ja foi aplicada antes de executar.
     */
    private static void executarMigracoes() throws Exception {
        Class.forName("org.sqlite.JDBC");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement  stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");

            if (!tabelaExiste(conn, "categorias") && !tabelaExiste(conn, "transacoes")) {
                criarSchemaInicial(stmt);
                return;
            }
            if (!tabelaExiste(conn, "categorias") || !tabelaExiste(conn, "transacoes")) {
                throw new IllegalStateException("Schema incompleto: tabelas categorias/transacoes inconsistentes.");
            }

            // ── Migracao 1: coluna 'tipo' na tabela 'categorias' ─────────────
            // Verifica se a coluna ja existe consultando o PRAGMA da tabela
            boolean colunaExiste = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(categorias)")) {
                while (rs.next()) {
                    if ("tipo".equalsIgnoreCase(rs.getString("name"))) {
                        colunaExiste = true;
                        break;
                    }
                }
            }

            if (!colunaExiste) {
                System.out.println("[Migracao] Adicionando coluna 'tipo' em 'categorias'...");
                // SQLite aceita ADD COLUMN nullable sem problemas
                stmt.execute("ALTER TABLE categorias ADD COLUMN tipo VARCHAR(10)");
                // Classifica todas as categorias existentes como DESPESA por padrao
                stmt.execute("UPDATE categorias SET tipo = 'DESPESA' WHERE tipo IS NULL");
                System.out.println("[Migracao] Coluna 'tipo' adicionada. " +
                        "Categorias existentes classificadas como DESPESA.");
            }

            validarTransacoesOrfas(conn);
            validarCategoriasDuplicadas(conn);
            criarIndiceUnicoCategorias(stmt);
            if (!transacoesPossuiForeignKey(conn)
                    || !tipoColunaEh(conn, "transacoes", "categoria_id", "integer")) {
                recriarTransacoesComForeignKey(conn);
            }
        }
    }

    /**
     * Executa a rotina tabelaExiste.
     *
     * @param conn parametro conn
     * @param nome parametro nome
     * @return true quando a condicao for atendida; false caso contrario
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static boolean tabelaExiste(Connection conn, String nome) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, nome, null)) {
            return rs.next();
        }
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param stmt parametro stmt
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void criarSchemaInicial(Statement stmt) throws Exception {
        stmt.execute("""
                CREATE TABLE categorias (
                    id integer,
                    tipo varchar(10) check (tipo in ('RECEITA','DESPESA')),
                    nome varchar(100) not null unique,
                    primary key (id)
                )
                """);
        criarIndiceUnicoCategorias(stmt);
        stmt.execute("""
                CREATE TABLE transacoes (
                    data date not null,
                    valor numeric(15,2) not null,
                    categoria_id integer,
                    id integer,
                    tipo varchar(10) not null check (tipo in ('RECEITA','DESPESA')),
                    descricao varchar(255) not null,
                    primary key (id),
                    foreign key (categoria_id) references categorias(id)
                )
                """);
    }

    /**
     * Executa a rotina validarTransacoesOrfas.
     *
     * @param conn parametro conn
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void validarTransacoesOrfas(Connection conn) throws Exception {
        String sql = """
                SELECT COUNT(*)
                FROM transacoes t
                WHERE t.categoria_id IS NOT NULL
                  AND NOT EXISTS (
                      SELECT 1 FROM categorias c WHERE c.id = t.categoria_id
                  )
                """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getLong(1) > 0) {
                throw new IllegalStateException(
                        "Existem transacoes com categoria_id sem categoria correspondente.");
            }
        }
    }

    /**
     * Executa a rotina validarCategoriasDuplicadas.
     *
     * @param conn parametro conn
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void validarCategoriasDuplicadas(Connection conn) throws Exception {
        String sql = """
                SELECT LOWER(nome), COUNT(*)
                FROM categorias
                GROUP BY LOWER(nome)
                HAVING COUNT(*) > 1
                """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                throw new IllegalStateException(
                        "Existem categorias duplicadas para o nome: " + rs.getString(1));
            }
        }
    }

    /**
     * Cria e configura o componente solicitado.
     *
     * @param stmt parametro stmt
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void criarIndiceUnicoCategorias(Statement stmt) throws Exception {
        stmt.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_categorias_nome_lower_unique
                ON categorias (LOWER(nome))
                """);
    }

    /**
     * Executa a rotina transacoesPossuiForeignKey.
     *
     * @param conn parametro conn
     * @return true quando a condicao for atendida; false caso contrario
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static boolean transacoesPossuiForeignKey(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(transacoes)")) {
            while (rs.next()) {
                if ("categorias".equalsIgnoreCase(rs.getString("table"))
                        && "categoria_id".equalsIgnoreCase(rs.getString("from"))
                        && "id".equalsIgnoreCase(rs.getString("to"))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Executa a rotina tipoColunaEh.
     *
     * @param conn parametro conn
     * @param tabela parametro tabela
     * @param coluna parametro coluna
     * @param tipo parametro tipo
     * @return true quando a condicao for atendida; false caso contrario
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static boolean tipoColunaEh(Connection conn, String tabela, String coluna, String tipo) throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tabela + ")")) {
            while (rs.next()) {
                if (coluna.equalsIgnoreCase(rs.getString("name"))) {
                    return tipo.equalsIgnoreCase(rs.getString("type"));
                }
            }
            return false;
        }
    }

    /**
     * Executa a rotina recriarTransacoesComForeignKey.
     *
     * @param conn parametro conn
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void recriarTransacoesComForeignKey(Connection conn) throws Exception {
        System.out.println("[Migracao] Recriando tabela 'transacoes' com foreign key...");
        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE transacoes RENAME TO transacoes_legacy_migracao");
            stmt.execute("""
                    CREATE TABLE transacoes (
                        data date not null,
                        valor numeric(15,2) not null,
                        categoria_id integer,
                        id integer,
                        tipo varchar(10) not null check (tipo in ('RECEITA','DESPESA')),
                        descricao varchar(255) not null,
                        primary key (id),
                        foreign key (categoria_id) references categorias(id)
                    )
                    """);
            stmt.execute("""
                    INSERT INTO transacoes (data, valor, categoria_id, id, tipo, descricao)
                    SELECT data, valor, categoria_id, id, tipo, descricao
                    FROM transacoes_legacy_migracao
                    """);
            stmt.execute("DROP TABLE transacoes_legacy_migracao");
            conn.commit();
            System.out.println("[Migracao] Tabela 'transacoes' recriada com foreign key.");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Cria e configura o componente solicitado.
     */
    private static void criarBackupInicial() {
        try {
            Path backup = SQLiteBackupUtil.criarBackup(DB_URL);
            System.out.println("[Sistema] Backup SQLite criado: " + backup);
        } catch (Exception e) {
            System.err.println("[Sistema] Backup SQLite nao foi criado: " + e.getMessage());
        }
    }
}
