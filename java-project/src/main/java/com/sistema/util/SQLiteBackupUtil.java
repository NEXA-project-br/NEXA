package com.sistema.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitario responsavel por criar copias de seguranca do banco SQLite.
 */
public final class SQLiteBackupUtil {

    /**
     * Diretorio onde os backups sao armazenados.
     */
    private static final Path BACKUP_DIR = DatabaseConfig.getBackupDirectory();
    /**
     * Formato usado para compor o nome dos arquivos de backup.
     */
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    /**
     * Cria uma nova instancia de SQLiteBackupUtil.
     */
    private SQLiteBackupUtil() {}

    /**
     * Cria uma copia de seguranca validada do banco de dados.
     *
     * @param dbUrl URL do banco de dados SQLite
     * @return caminho do arquivo gerado
     * @throws Exception se a operacao nao puder ser concluida
     */
    public static Path criarBackup(String dbUrl) throws Exception {
        Files.createDirectories(BACKUP_DIR);

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            validarBanco(stmt);

            Path destino = proximoDestino();
            stmt.execute("VACUUM INTO '" + escaparSql(destino.toAbsolutePath().toString()) + "'");
            return destino;
        }
    }

    /**
     * Executa a rotina validarBanco.
     *
     * @param stmt parametro stmt
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static void validarBanco(Statement stmt) throws Exception {
        try (ResultSet rs = stmt.executeQuery("PRAGMA quick_check")) {
            if (!rs.next() || !"ok".equalsIgnoreCase(rs.getString(1))) {
                throw new IllegalStateException("PRAGMA quick_check falhou.");
            }
        }
        try (ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_check")) {
            if (rs.next()) {
                throw new IllegalStateException("PRAGMA foreign_key_check encontrou inconsistencias.");
            }
        }
    }

    /**
     * Executa a rotina proximoDestino.
     *
     * @return caminho do arquivo gerado
     * @throws Exception se a operacao nao puder ser concluida
     */
    private static Path proximoDestino() throws Exception {
        for (int tentativa = 0; tentativa < 3; tentativa++) {
            Path destino = BACKUP_DIR.resolve("backup-" + LocalDateTime.now().format(FMT) + ".db");
            if (!Files.exists(destino)) {
                return destino;
            }
            Thread.sleep(1000);
        }
        throw new IllegalStateException("Nao foi possivel gerar um nome unico para o backup.");
    }

    /**
     * Executa a rotina escaparSql.
     *
     * @param valor parametro valor
     * @return texto formatado
     */
    private static String escaparSql(String valor) {
        return valor.replace("'", "''");
    }
}
