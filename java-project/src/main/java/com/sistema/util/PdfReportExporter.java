package com.sistema.util;

import com.sistema.controller.TransacaoController.ResumoFinanceiro;
import com.sistema.model.Transacao;
import com.sistema.model.TipoTransacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PdfReportExporter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN_X = 48;
    private static final int START_Y = 790;
    private static final int LINE_HEIGHT = 16;

    private PdfReportExporter() {}

    public static void exportar(ResumoFinanceiro resumo, Path destino) throws IOException {
        if (resumo == null) {
            throw new IllegalArgumentException("Resumo financeiro nao pode ser nulo.");
        }
        if (destino == null) {
            throw new IllegalArgumentException("Arquivo de destino nao pode ser nulo.");
        }

        Path parent = destino.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<List<String>> paginas = quebrarEmPaginas(montarLinhas(resumo));
        escreverPdf(paginas, destino);
    }

    private static List<String> montarLinhas(ResumoFinanceiro resumo) {
        List<String> linhas = new ArrayList<>();
        linhas.add("Relatorio Financeiro");
        linhas.add("Periodo: " + resumo.inicio().format(FMT) + " a " + resumo.fim().format(FMT));
        linhas.add("");
        linhas.add("Receitas: " + CurrencyUtil.formatar(resumo.totalReceitas()));
        linhas.add("Despesas: " + CurrencyUtil.formatar(resumo.totalDespesas()));
        linhas.add("Saldo: " + CurrencyUtil.formatar(resumo.saldo()));
        linhas.add("");
        linhas.add("Data       Tipo      Categoria           Valor          Descricao");
        linhas.add("-----------------------------------------------------------------------");

        if (resumo.transacoes().isEmpty()) {
            linhas.add("Nenhuma transacao encontrada no periodo.");
            return linhas;
        }

        for (Transacao transacao : resumo.transacoes()) {
            linhas.add(formatarTransacao(transacao));
        }
        return linhas;
    }

    private static String formatarTransacao(Transacao transacao) {
        String data = transacao.getData().format(FMT);
        String tipo = transacao.getTipo() == TipoTransacao.RECEITA ? "Receita" : "Despesa";
        String categoria = transacao.getCategoria() != null ? transacao.getCategoria().getNome() : "-";
        BigDecimal valor = transacao.getValor();
        String descricao = transacao.getDescricao() != null ? transacao.getDescricao() : "";

        return pad(data, 10)
                + " " + pad(tipo, 8)
                + " " + pad(limitar(categoria, 18), 18)
                + " " + pad(CurrencyUtil.formatar(valor), 14)
                + " " + limitar(descricao, 42);
    }

    private static List<List<String>> quebrarEmPaginas(List<String> linhas) {
        int linhasPorPagina = 42;
        List<List<String>> paginas = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i += linhasPorPagina) {
            paginas.add(linhas.subList(i, Math.min(i + linhasPorPagina, linhas.size())));
        }
        return paginas;
    }

    private static void escreverPdf(List<List<String>> paginas, Path destino) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        List<byte[]> streams = new ArrayList<>();

        for (int i = 0; i < paginas.size(); i++) {
            streams.add(criarConteudoPagina(paginas.get(i), i + 1, paginas.size()));
        }

        writeAscii(out, "%PDF-1.4\n");
        offsets.add(0);

        appendObject(out, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < paginas.size(); i++) {
            kids.append(3 + (i * 2)).append(" 0 R ");
        }
        appendObject(out, offsets, 2, "<< /Type /Pages /Kids [" + kids + "] /Count " + paginas.size() + " >>");

        for (int i = 0; i < paginas.size(); i++) {
            int pageObj = 3 + (i * 2);
            int contentObj = pageObj + 1;
            appendObject(out, offsets, pageObj,
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                            + "] /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >> "
                            + "/F2 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >> "
                            + "/F3 << /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >> >> >> "
                            + "/Contents " + contentObj + " 0 R >>");
            appendStreamObject(out, offsets, contentObj, streams.get(i));
        }

        int xrefStart = out.size();
        writeAscii(out, "xref\n0 " + offsets.size() + "\n");
        writeAscii(out, "0000000000 65535 f \n");
        for (int i = 1; i < offsets.size(); i++) {
            writeAscii(out, "%010d 00000 n \n".formatted(offsets.get(i)));
        }
        writeAscii(out, "trailer\n<< /Size " + offsets.size() + " /Root 1 0 R >>\n");
        writeAscii(out, "startxref\n" + xrefStart + "\n%%EOF\n");

        Files.write(destino, out.toByteArray());
    }

    private static byte[] criarConteudoPagina(List<String> linhas, int paginaAtual, int totalPaginas) {
        ByteArrayOutputStream page = new ByteArrayOutputStream();
        writeAscii(page, "BT\n");

        int y = START_Y;
        for (int i = 0; i < linhas.size(); i++) {
            boolean titulo = paginaAtual == 1 && i == 0;
            boolean linhaTabela = i >= 7;
            String fonte = titulo ? "/F2 18 Tf\n" : linhaTabela ? "/F3 9 Tf\n" : "/F1 10 Tf\n";
            writeAscii(page, fonte);
            writeAscii(page, "1 0 0 1 " + MARGIN_X + " " + y + " Tm\n");
            writeAscii(page, "(" + escapar(linhas.get(i)) + ") Tj\n");
            y -= titulo ? 24 : LINE_HEIGHT;
        }

        writeAscii(page, "/F1 9 Tf\n");
        writeAscii(page, "1 0 0 1 " + MARGIN_X + " 34 Tm\n");
        writeAscii(page, "(Pagina " + paginaAtual + " de " + totalPaginas + ") Tj\n");
        writeAscii(page, "ET\n");
        return page.toByteArray();
    }

    private static void appendObject(ByteArrayOutputStream out, List<Integer> offsets, int numero, String conteudo) {
        offsets.add(out.size());
        writeAscii(out, numero + " 0 obj\n" + conteudo + "\nendobj\n");
    }

    private static void appendStreamObject(ByteArrayOutputStream out, List<Integer> offsets, int numero, byte[] stream) {
        offsets.add(out.size());
        writeAscii(out, numero + " 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
        out.writeBytes(stream);
        writeAscii(out, "endstream\nendobj\n");
    }

    private static String escapar(String texto) {
        return normalizar(texto)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static String normalizar(String texto) {
        return texto == null ? "" : texto
                .replace("\u00a0", " ")
                .replace("\u2013", "-")
                .replace("\u2014", "-")
                .replace("\u201c", "\"")
                .replace("\u201d", "\"")
                .replace("\u2019", "'");
    }

    private static String limitar(String texto, int tamanho) {
        String valor = normalizar(texto);
        if (valor.length() <= tamanho) {
            return valor;
        }
        return valor.substring(0, Math.max(0, tamanho - 3)) + "...";
    }

    private static String pad(String texto, int tamanho) {
        String valor = limitar(texto, tamanho);
        return valor + " ".repeat(Math.max(0, tamanho - valor.length()));
    }

    private static void writeAscii(ByteArrayOutputStream out, String texto) {
        out.writeBytes(texto.getBytes(StandardCharsets.ISO_8859_1));
    }
}
