package com.sistema;

import com.sistema.util.HibernateUtil;
import com.sistema.view.MainView;

import javax.swing.*;
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

    private static final String DB_URL = "jdbc:sqlite:financeiro.db";

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
            } catch (Exception ignored) {
                // Tabela ainda nao existe — o Hibernate vai criá-la do zero, OK
                return;
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
        }
    }
}