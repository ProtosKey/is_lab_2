package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.HumanRef;
import dev.protoskey.model.HumanRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HumanRefValidator implements Validator<HumanRef> {
    @Inject
    private HumanRequestValidator humanValidator;

    @Override
    public void validate(HumanRef ref) throws ValidatorException {
        if (ref == null) {
            throw new ValidatorException("Губернатор не может быть пустым");
        }
        if (ref.getId() != null) {
            validateId(ref.getId());
            return;
        }
        HumanRequest request = new HumanRequest();
        request.setName(ref.getName());
        request.setAge(ref.getAge());
        request.setBirthday(ref.getBirthday());
        humanValidator.validate(request);
    }

    public void validateId(Integer id) throws ValidatorException {
        if (id == null || id <= 0) {
            throw new ValidatorException("Идентификатор губернатора должен быть положительным числом");
        }
    }
}
