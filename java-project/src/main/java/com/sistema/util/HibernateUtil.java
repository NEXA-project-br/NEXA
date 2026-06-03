package com.sistema.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

/**
 * Utilitário para gerenciar o ciclo de vida do EntityManagerFactory.
 * Implementa o padrão Singleton garantindo uma única instância de fábrica
 * por aplicação, conforme boas práticas do Hibernate/JPA.
 */
public class HibernateUtil {

    /**
     * Nome da unidade de persistencia configurada no projeto.
     */
    private static final String PERSISTENCE_UNIT = "sistemaFinanceiroPU";
    /**
     * Fabrica compartilhada de EntityManager.
     */
    private static EntityManagerFactory factory;

    /**
     * Cria uma nova instancia de HibernateUtil.
     */
    private HibernateUtil() {}

    /**
     * Retorna a instância única do EntityManagerFactory.
     * Inicialização lazy e thread-safe via synchronized.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (factory == null || !factory.isOpen()) {
            Map<String, String> properties = Map.of(
                    "jakarta.persistence.jdbc.url", DatabaseConfig.getJdbcUrl()
            );
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
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
