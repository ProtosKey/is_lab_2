package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;

public interface Validator<T> {
    void validate(T t) throws ValidatorException;
}
