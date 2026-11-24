package dev.protoskey.repository;

import dev.protoskey.entity.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OperationRepository implements Repository<Operation> {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    @Override
    public Optional<Operation> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Operation.class, id));
    }

    @Override
    public List<Operation> findAll() {
        return entityManager.createQuery(
                        "select o from Operation o order by o.createdAt desc, o.id desc", Operation.class)
                .getResultList();
    }

    public List<Operation> findAllByUser(Integer userId) {
        return entityManager.createQuery(
                        "select o from Operation o where o.user.id = :userId order by o.createdAt desc, o.id desc",
                        Operation.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Operation> findPage(Integer cursor, int limit) {
        Operation anchor = cursor != null ? entityManager.find(Operation.class, cursor) : null;
        String jpql = anchor == null
                ? "select o from Operation o order by o.createdAt desc, o.id desc"
                : "select o from Operation o where (o.createdAt < :cursorTime or (o.createdAt = :cursorTime and o.id < :cursorId)) order by o.createdAt desc, o.id desc";
        var query = entityManager.createQuery(jpql, Operation.class);
        if (anchor != null) {
            query.setParameter("cursorTime", anchor.getCreatedAt());
            query.setParameter("cursorId", anchor.getId());
        }
        return query.setMaxResults(Math.max(1, limit) + 1).getResultList();
    }

    public List<Operation> findPageByUser(Integer userId, Integer cursor, int limit) {
        Operation anchor = cursor != null ? entityManager.find(Operation.class, cursor) : null;
        String jpql = anchor == null
                ? "select o from Operation o where o.user.id = :userId order by o.createdAt desc, o.id desc"
                : "select o from Operation o where o.user.id = :userId and (o.createdAt < :cursorTime or (o.createdAt = :cursorTime and o.id < :cursorId)) order by o.createdAt desc, o.id desc";
        var query = entityManager.createQuery(jpql, Operation.class)
                .setParameter("userId", userId);
        if (anchor != null) {
            query.setParameter("cursorTime", anchor.getCreatedAt());
            query.setParameter("cursorId", anchor.getId());
        }
        return query.setMaxResults(Math.max(1, limit) + 1).getResultList();
    }

    public long countAll() {
        return entityManager.createQuery("select count(o) from Operation o", Long.class)
                .getSingleResult();
    }

    public long countByUser(Integer userId) {
        return entityManager.createQuery("select count(o) from Operation o where o.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public Operation save(Operation operation) {
        entityManager.persist(operation);
        return operation;
    }

    @Override
    public Operation update(Operation operation) {
        return entityManager.merge(operation);
    }

    @Override
    public void delete(Operation operation) {
        entityManager.remove(entityManager.contains(operation) ? operation : entityManager.merge(operation));
    }
}
