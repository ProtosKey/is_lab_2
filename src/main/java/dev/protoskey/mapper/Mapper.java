package dev.protoskey.mapper;

import dev.protoskey.exception.MapperException;

public interface Mapper<E, D> {
    D parseFrom(E e) throws MapperException;
    E parseTo(D d) throws MapperException;
}
