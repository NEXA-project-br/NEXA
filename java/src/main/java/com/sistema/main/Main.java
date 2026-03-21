package com.sistema.main;

import com.sistema.util.HibernateUtil;
import com.sistema.view.MainView;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação Sistema Financeiro Pessoal.
 *
 * Responsabilidades:
 * - Inicializa o Look and Feel do sistema
 * - Cria e inicializa o EntityManagerFactory (Hibernate/SQLite)
 * - Exibe a tela principal na Event Dispatch Thread (EDT)
 * - Garante o desligamento correto do Hibernate ao fechar a JVM
 */
public class Main {

    public static void main(String[] args) {
        // Registra hook de encerramento para fechar Hibernate corretamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Sistema] Encerrando — fechando EntityManagerFactory...");
            HibernateUtil.shutdown();
        }));

        // Inicializa o Hibernate antes de exibir a UI (cria tabelas se necessário)
        try {
            System.out.println("[Sistema] Inicializando banco de dados...");
            HibernateUtil.getEntityManagerFactory();
            System.out.println("[Sistema] Banco de dados inicializado com sucesso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao inicializar o banco de dados:\n" + e.getMessage(),
                    "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Configura Look and Feel nativo do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback para L&F padrão Java (Metal)
        }

        // Inicializa e exibe a tela principal na EDT
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}