package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportCityDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ImportCityDtoValidator implements Validator<ImportCityDto> {
    @Inject
    private CityRequestValidator cityValidator;

    @Override
    public void validate(ImportCityDto dto) throws ValidatorException {
        if (dto == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        cityValidator.validate(dto);
    }
}
