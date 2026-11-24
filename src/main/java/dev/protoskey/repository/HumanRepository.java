package dev.protoskey.repository;

import dev.protoskey.entity.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HumanRepository implements Repository<Human> {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    public Optional<Human> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Human.class, id));
    }

    public List<Human> findAll() {
        return entityManager.createQuery("select h from Human h order by h.id", Human.class).getResultList();
    }

    public List<Human> findPage(Integer cursor, int limit) {
        String jpql = cursor == null
                ? "select h from Human h order by h.id asc"
                : "select h from Human h where h.id > :cursor order by h.id asc";
        var query = entityManager.createQuery(jpql, Human.class);
        if (cursor != null) {
            query.setParameter("cursor", cursor);
        }
        return query.setMaxResults(Math.max(1, limit) + 1).getResultList();
    }

    public long count() {
        return entityManager.createQuery("select count(h) from Human h", Long.class)
                .getSingleResult();
    }

    public Optional<Human> findByName(String name) {
        return entityManager.createQuery("select h from Human h where h.name = :name", Human.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Human save(Human human) {
        entityManager.persist(human);
        return human;
    }

    public Human update(Human human) {
        return entityManager.merge(human);
    }

    public void delete(Human human) {
        entityManager.remove(entityManager.contains(human) ? human : entityManager.merge(human));
    }
}
