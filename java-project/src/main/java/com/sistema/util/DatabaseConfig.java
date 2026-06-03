package com.sistema.util;

import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza os caminhos e URLs usados pelo banco de dados da aplicacao.
 */
public final class DatabaseConfig {

    /**
     * Nome do arquivo SQLite usado pela aplicacao.
     */
    private static final String DATABASE_FILE = "financeiro.db";
    /**
     * Nome da pasta de dados da aplicacao.
     */
    private static final String APP_DIR_NAME = "NEXA";
    /**
     * Propriedade Java para sobrescrever o caminho do banco.
     */
    private static final String DB_PATH_PROPERTY = "nexa.db.path";
    /**
     * Variavel de ambiente para sobrescrever o caminho do banco.
     */
    private static final String DB_PATH_ENV = "NEXA_DB_PATH";

    /**
     * Cria uma nova instancia de DatabaseConfig.
     */
    private DatabaseConfig() {}

    /**
     * Retorna o caminho absoluto do banco de dados usado pela aplicacao.
     *
     * @return caminho do arquivo SQLite
     */
    public static Path getDatabasePath() {
        String override = System.getProperty(DB_PATH_PROPERTY);
        if (override == null || override.isBlank()) {
            override = System.getenv(DB_PATH_ENV);
        }
        if (override != null && !override.isBlank()) {
            return Path.of(override).toAbsolutePath().normalize();
        }
        return getAppDataDirectory().resolve(DATABASE_FILE);
    }

    /**
     * Retorna o diretorio onde os dados da aplicacao sao armazenados.
     *
     * @return diretorio de dados
     */
    public static Path getAppDataDirectory() {
        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.isBlank()) {
            return Path.of(appData).resolve(APP_DIR_NAME).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.home"), "." + APP_DIR_NAME.toLowerCase())
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Retorna o diretorio onde os backups SQLite sao armazenados.
     *
     * @return diretorio de backups
     */
    public static Path getBackupDirectory() {
        return getDatabasePath().getParent().resolve("backup");
    }

    /**
     * Retorna a URL JDBC do SQLite usada por JDBC direto e Hibernate.
     *
     * @return URL JDBC
     */
    public static String getJdbcUrl() {
        return "jdbc:sqlite:" + toJdbcPath(getDatabasePath()) + "?foreign_keys=on";
    }

    /**
     * Copia o banco legado relativo para o local fixo da aplicacao, quando
     * o novo banco ainda nao existe.
     *
     * @throws Exception se a copia ou validacao nao puder ser concluida
     */
    public static void migrarBancoLegadoSeNecessario() throws Exception {
        Path destino = getDatabasePath();
        Files.createDirectories(destino.getParent());

        if (Files.exists(destino)) {
            return;
        }

        Path origem = localizarBancoLegado(destino);
        if (origem == null) {
            return;
        }

        Path temporario = destino.resolveSibling(destino.getFileName() + ".tmp");
        Files.deleteIfExists(temporario);
        Files.copy(origem, temporario, StandardCopyOption.COPY_ATTRIBUTES);

        try {
            validarBanco(temporario);
            moverCopiaValidada(temporario, destino);
            System.out.println("[Sistema] Banco legado migrado de " + origem + " para " + destino);
        } catch (Exception e) {
            Files.deleteIfExists(temporario);
            throw e;
        }
    }

    /**
     * Procura bancos legados em locais comuns de execucao do projeto.
     *
     * @param destino caminho do banco novo
     * @return caminho do banco legado ou null
     */
    private static Path localizarBancoLegado(Path destino) {
        for (Path candidato : candidatosBancoLegado()) {
            Path normalizado = candidato.toAbsolutePath().normalize();
            if (Files.exists(normalizado) && !normalizado.equals(destino)) {
                return normalizado;
            }
        }
        return null;
    }

    /**
     * Retorna candidatos de banco legado relativo usados antes da correcao.
     *
     * @return lista de candidatos
     */
    private static List<Path> candidatosBancoLegado() {
        List<Path> candidatos = new ArrayList<>();
        candidatos.add(Path.of(DATABASE_FILE));
        candidatos.add(Path.of("java-project", DATABASE_FILE));
        return candidatos;
    }

    /**
     * Valida a integridade da copia antes de torna-la o banco oficial.
     *
     * @param dbPath caminho do banco copiado
     * @throws Exception se a validacao falhar
     */
    private static void validarBanco(Path dbPath) throws Exception {
        String url = "jdbc:sqlite:" + toJdbcPath(dbPath.toAbsolutePath().normalize()) + "?foreign_keys=on";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            try (ResultSet rs = stmt.executeQuery("PRAGMA quick_check")) {
                if (!rs.next() || !"ok".equalsIgnoreCase(rs.getString(1))) {
                    throw new IllegalStateException("PRAGMA quick_check falhou na copia do banco.");
                }
            }
            try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_check")) {
                if (rs.next()) {
                    throw new IllegalStateException(
                            "PRAGMA foreign_key_check encontrou inconsistencias na copia do banco.");
                }
            }
        }
    }

    /**
     * Move a copia validada para o destino final.
     *
     * @param origem arquivo temporario validado
     * @param destino destino final
     * @throws Exception se a movimentacao nao puder ser concluida
     */
    private static void moverCopiaValidada(Path origem, Path destino) throws Exception {
        try {
            Files.move(origem, destino, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(origem, destino);
        }
    }

    /**
     * Normaliza o caminho para a forma mais previsivel em URLs SQLite no Windows.
     *
     * @param path caminho do banco
     * @return caminho para compor URL JDBC
     */
    private static String toJdbcPath(Path path) {
        return path.toAbsolutePath().normalize().toString().replace('\\', '/');
    }
}
