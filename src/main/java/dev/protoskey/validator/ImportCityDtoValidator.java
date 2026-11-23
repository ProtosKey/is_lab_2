package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportCityDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ImportCityDtoValidator implements Validator<ImportCityDto> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final CityRequestValidator cityValidator = new CityRequestValidator();

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
            LocalDateTime dateTime = LocalDateTime.parse(creationDate, DATE_TIME_FORMATTER);
            if (dateTime.isAfter(LocalDateTime.now())) {
                throw new ValidatorException("Дата создания не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты создания. Ожидается формат yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}
