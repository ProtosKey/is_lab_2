package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CoordinatesDto;

public class CoordinatesDtoValidator implements Validator<CoordinatesDto> {
    private static final int MAX_VALUE = 974;

    @Override
    public void validate(CoordinatesDto dto) throws ValidatorException {
        if (dto == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        validateX(dto.getX());
        validateY(dto.getY());
    }

    public void validateX(Integer x) throws ValidatorException {
        if (x == null) {
            throw new ValidatorException("Абсцисса не может быть пустой");
        }
        if (x > MAX_VALUE) {
            throw new ValidatorException("Абсцисса не может быть больше 974");
        }
    }

    public void validateY(Integer y) throws ValidatorException {
        if (y == null) {
            throw new ValidatorException("Ордината не может быть пустой");
        }
    }
}
