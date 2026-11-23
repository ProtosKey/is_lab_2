package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CoordinatesRef;

public class CoordinatesRefValidator implements Validator<CoordinatesRef> {
    private final CoordinatesRequestValidator coordinatesValidator = new CoordinatesRequestValidator();

    @Override
    public void validate(CoordinatesRef ref) throws ValidatorException {
        if (ref == null) {
            throw new ValidatorException("Координаты не могут быть пустыми");
        }
        if (ref.getId() != null) {
            validateId(ref.getId());
            return;
        }
        coordinatesValidator.validateX(ref.getX());
        coordinatesValidator.validateY(ref.getY());
    }

    public void validateId(Integer id) throws ValidatorException {
        if (id == null || id <= 0) {
            throw new ValidatorException("Идентификатор координат должен быть положительным числом");
        }
    }
}
