package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CoordinatesRef;
import dev.protoskey.model.CoordinatesRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CoordinatesRefValidator implements Validator<CoordinatesRef> {
    @Inject
    private CoordinatesRequestValidator coordinatesValidator;

    @Override
    public void validate(CoordinatesRef ref) throws ValidatorException {
        if (ref == null) {
            throw new ValidatorException("Координаты не могут быть пустыми");
        }
        if (ref.getId() != null) {
            validateId(ref.getId());
            return;
        }
        CoordinatesRequest request = new CoordinatesRequest();
        request.setX(ref.getX());
        request.setY(ref.getY());
        coordinatesValidator.validate(request);
    }

    public void validateId(Integer id) throws ValidatorException {
        if (id == null || id <= 0) {
            throw new ValidatorException("Идентификатор координат должен быть положительным числом");
        }
    }
}
