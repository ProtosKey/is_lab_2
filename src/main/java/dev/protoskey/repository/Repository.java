package dev.protoskey.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<E> {
    Optional<E> findById(Integer id);

    List<E> findAll();

    E save(E entity);

    E update(E entity);

    void delete(E entity);
}
