package com.sistema.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SQLiteBackupUtil {

    private static final Path BACKUP_DIR = Path.of("backup");
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private SQLiteBackupUtil() {}

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

    private static String escaparSql(String valor) {
        return valor.replace("'", "''");
    }
}
