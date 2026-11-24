package dev.protoskey.repository;

import dev.protoskey.entity.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesRepository implements Repository<Coordinates> {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    public Optional<Coordinates> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Coordinates.class, id));
    }

    public List<Coordinates> findAll() {
        return entityManager.createQuery("select c from Coordinates c order by c.id", Coordinates.class)
                .getResultList();
    }

    public List<Coordinates> findPage(int offset, int limit) {
        return entityManager.createQuery("select c from Coordinates c order by c.id", Coordinates.class)
                .setFirstResult(Math.max(0, offset))
                .setMaxResults(Math.max(1, limit))
                .getResultList();
    }

    public long count() {
        return entityManager.createQuery("select count(c) from Coordinates c", Long.class)
                .getSingleResult();
    }

    public Optional<Coordinates> findByCoordinates(Integer x, Integer y) {
        return entityManager.createQuery(
                        "select c from Coordinates c where c.x = :x and c.y = :y", Coordinates.class)
                .setParameter("x", x)
                .setParameter("y", y)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Coordinates save(Coordinates coordinates) {
        entityManager.persist(coordinates);
        return coordinates;
    }

    public Coordinates update(Coordinates coordinates) {
        return entityManager.merge(coordinates);
    }

    public void delete(Coordinates coordinates) {
        entityManager.remove(entityManager.contains(coordinates) ? coordinates : entityManager.merge(coordinates));
    }
}
