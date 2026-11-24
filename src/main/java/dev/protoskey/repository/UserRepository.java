package dev.protoskey.repository;

import dev.protoskey.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements Repository<User> {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("select u from User u order by u.id", User.class).getResultList();
    }

    public Optional<User> findByLogin(String login) {
        return entityManager.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
