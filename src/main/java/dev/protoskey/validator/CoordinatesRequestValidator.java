package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CoordinatesRequest;

public class CoordinatesRequestValidator implements Validator<CoordinatesRequest> {
    private static final int MAX_VALUE = 974;

    @Override
    public void validate(CoordinatesRequest request) throws ValidatorException {
        if (request == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        validateX(request.getX());
        validateY(request.getY());
    }

    public void validateX(Integer x) throws ValidatorException {
        if (x == null) {
            throw new ValidatorException("Абсцисса не может быть пустой");
        }
        if (x > MAX_VALUE) {
            throw new ValidatorException("Абсцисса не может быть больше " + MAX_VALUE);
        }
    }

    public void validateY(Integer y) throws ValidatorException {
        if (y == null) {
            throw new ValidatorException("Ордината не может быть пустой");
        }
    }
}
