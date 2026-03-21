package com.sistema.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitário para gerenciar o ciclo de vida do EntityManagerFactory.
 * Implementa o padrão Singleton garantindo uma única instância de fábrica
 * por aplicação, conforme boas práticas do Hibernate/JPA.
 */
public class HibernateUtil {

    private static final String PERSISTENCE_UNIT = "sistemaFinanceiroPU";
    private static EntityManagerFactory factory;

    private HibernateUtil() {}

    /**
     * Retorna a instância única do EntityManagerFactory.
     * Inicialização lazy e thread-safe via synchronized.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return factory;
    }

    /**
     * Cria e retorna um novo EntityManager a partir da fábrica.
     * O chamador é responsável por fechar o EntityManager após o uso.
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory liberando todos os recursos.
     * Deve ser chamado ao encerrar a aplicação.
     */
    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}