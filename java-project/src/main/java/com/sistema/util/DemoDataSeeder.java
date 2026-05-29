package com.sistema.util;

import com.sistema.Main;
import com.sistema.model.Categoria;
import com.sistema.model.TipoTransacao;
import com.sistema.model.Transacao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Preenche o banco com dados ficticios para demonstracao.
 */
public final class DemoDataSeeder {

    private DemoDataSeeder() {
    }

    /**
     * Permite rodar a seed manualmente pela linha de comando ou pela IDE.
     *
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        try {
            Main.prepararBanco();
            HibernateUtil.getEntityManagerFactory();
            executarSeNecessario();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao executar seed de demonstracao.", e);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    /**
     * Cria categorias e transacoes de exemplo quando o banco ainda esta vazio.
     */
    public static void executarSeNecessario() {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Long totalTransacoes = em.createQuery(
                            "SELECT COUNT(t) FROM Transacao t",
                            Long.class)
                    .getSingleResult();

            if (totalTransacoes > 0) {
                System.out.println("[Seed] Banco ja possui transacoes. Seed ignorada.");
                return;
            }

            tx.begin();

            Categoria salario = obterOuCriarCategoria(em, "Salario", TipoTransacao.RECEITA);
            Categoria freelance = obterOuCriarCategoria(em, "Freelance", TipoTransacao.RECEITA);
            Categoria investimentos = obterOuCriarCategoria(em, "Investimentos", TipoTransacao.RECEITA);
            Categoria alimentacao = obterOuCriarCategoria(em, "Alimentacao", TipoTransacao.DESPESA);
            Categoria moradia = obterOuCriarCategoria(em, "Moradia", TipoTransacao.DESPESA);
            Categoria transporte = obterOuCriarCategoria(em, "Transporte", TipoTransacao.DESPESA);
            Categoria saude = obterOuCriarCategoria(em, "Saude", TipoTransacao.DESPESA);
            Categoria lazer = obterOuCriarCategoria(em, "Lazer", TipoTransacao.DESPESA);
            Categoria educacao = obterOuCriarCategoria(em, "Educacao", TipoTransacao.DESPESA);
            Categoria assinaturas = obterOuCriarCategoria(em, "Assinaturas", TipoTransacao.DESPESA);

            LocalDate hoje = LocalDate.now();

            for (int mes = 5; mes >= 0; mes--) {
                LocalDate referencia = hoje.minusMonths(mes);
                BigDecimal variacaoSalario = BigDecimal.valueOf((5 - mes) * 80L);

                salvarTransacao(em, "Salario mensal",
                        new BigDecimal("4200.00").add(variacaoSalario),
                        referencia.withDayOfMonth(5), TipoTransacao.RECEITA, salario);
                salvarTransacao(em, "Projeto freelancer",
                        BigDecimal.valueOf(650L + (mes % 3) * 180L),
                        referencia.withDayOfMonth(12), TipoTransacao.RECEITA, freelance);
                salvarTransacao(em, "Rendimento de investimento",
                        BigDecimal.valueOf(95L + (5 - mes) * 18L),
                        referencia.withDayOfMonth(26), TipoTransacao.RECEITA, investimentos);

                salvarTransacao(em, "Aluguel",
                        new BigDecimal("1450.00"),
                        referencia.withDayOfMonth(8), TipoTransacao.DESPESA, moradia);
                salvarTransacao(em, "Mercado do mes",
                        BigDecimal.valueOf(720L + mes * 35L),
                        referencia.withDayOfMonth(10), TipoTransacao.DESPESA, alimentacao);
                salvarTransacao(em, "Transporte e combustivel",
                        BigDecimal.valueOf(260L + mes * 20L),
                        referencia.withDayOfMonth(14), TipoTransacao.DESPESA, transporte);
                salvarTransacao(em, "Plano de saude",
                        new BigDecimal("310.00"),
                        referencia.withDayOfMonth(18), TipoTransacao.DESPESA, saude);
                salvarTransacao(em, "Lazer e restaurantes",
                        BigDecimal.valueOf(280L + (mes % 2) * 120L),
                        referencia.withDayOfMonth(21), TipoTransacao.DESPESA, lazer);
                salvarTransacao(em, "Curso online",
                        BigDecimal.valueOf(mes % 2 == 0 ? 180L : 0L),
                        referencia.withDayOfMonth(23), TipoTransacao.DESPESA, educacao);
                salvarTransacao(em, "Streaming e aplicativos",
                        new BigDecimal("89.90"),
                        referencia.withDayOfMonth(27), TipoTransacao.DESPESA, assinaturas);
            }

            tx.commit();
            System.out.println("[Seed] Dados ficticios de demonstracao criados com sucesso.");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new IllegalStateException("Erro ao criar dados ficticios de demonstracao.", e);
        } finally {
            em.close();
        }
    }

    private static Categoria obterOuCriarCategoria(EntityManager em, String nome, TipoTransacao tipo) {
        return em.createQuery(
                        "SELECT c FROM Categoria c WHERE LOWER(c.nome) = LOWER(:nome)",
                        Categoria.class)
                .setParameter("nome", nome)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    Categoria categoria = new Categoria(nome, tipo);
                    em.persist(categoria);
                    return categoria;
                });
    }

    private static void salvarTransacao(EntityManager em, String descricao, BigDecimal valor,
                                        LocalDate data, TipoTransacao tipo, Categoria categoria) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        em.persist(new Transacao(descricao, valor, data, tipo, categoria));
    }
}
