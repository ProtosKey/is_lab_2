package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportCityDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@ApplicationScoped
public class ImportCityDtoValidator implements Validator<ImportCityDto> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Inject
    private CityRequestValidator cityValidator;

    @Override
    public void validate(ImportCityDto dto) throws ValidatorException {
        if (dto == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        cityValidator.validate(dto);
        validateCreationDate(dto.getCreationDate());
    }

    public void validateCreationDate(String creationDate) throws ValidatorException {
        if (creationDate == null || creationDate.isBlank()) {
            throw new ValidatorException("Дата создания не может быть пустой");
        }
        try {
            LocalDate date = LocalDate.parse(creationDate, FORMATTER);
            if (date.isAfter(LocalDate.now())) {
                throw new ValidatorException("Дата создания не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты создания. Ожидается формат yyyy-MM-dd");
        }
    }
}
