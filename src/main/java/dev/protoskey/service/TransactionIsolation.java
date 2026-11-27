package dev.protoskey.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TransactionIsolation {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    public void serializable() {
        entityManager.createNativeQuery("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE").executeUpdate();
    }
}
