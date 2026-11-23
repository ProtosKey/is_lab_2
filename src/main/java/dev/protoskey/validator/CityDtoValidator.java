package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CityDto;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CityDtoValidator implements Validator<CityDto> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public void validate(CityDto dto) throws ValidatorException {
        if (dto == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        validateName(dto.getName());
        validateCoordinatesId(dto.getCoordinatesId());
        validateCoordinatesX(dto.getCoordinatesX());
        validateCoordinatesY(dto.getCoordinatesY());
        validateCreationDate(dto.getCreationDate());
        validateArea(dto.getArea());
        validatePopulation(dto.getPopulation());
        validateEstablishmentDate(dto.getEstablishmentDate());
        validateCapital(dto.getCapital());
        validateMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        validatePopulationDensity(dto.getPopulationDensity());
        validateClimate(dto.getClimate());
        validateStandardOfLiving(dto.getStandardOfLiving());
        validateGovernorId(dto.getGovernorId());
        validateGovernorName(dto.getGovernorName());
        validateGovernorAge(dto.getGovernorAge());
        validateGovernorBirthday(dto.getGovernorBirthday());
    }

    public void validateName(String name) throws ValidatorException {
        if (name == null || name.isBlank()) {
            throw new ValidatorException("Название города не может быть пустым");
        }
    }

    public void validateCoordinatesId(Integer coordinatesId) throws ValidatorException {
        if (coordinatesId == null) {
            throw new ValidatorException("ID координат не может быть пустым");
        }
    }

    public void validateCoordinatesX(Integer coordinatesX) throws ValidatorException {
        if (coordinatesX == null) {
            throw new ValidatorException("Абсцисса не может быть пустой");
        }
        if (coordinatesX > 974) {
            throw new ValidatorException("Абсцисса не может быть больше 974");
        }
    }

    public void validateCoordinatesY(Integer coordinatesY) throws ValidatorException {
        if (coordinatesY == null) {
            throw new ValidatorException("Ордината не может быть пустой");
        }
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

    public void validateArea(Integer area) throws ValidatorException {
        if (area == null) {
            throw new ValidatorException("Площадь не может быть пустой");
        }
        if (area <= 0) {
            throw new ValidatorException("Площадь должна быть положительной");
        }
    }

    public void validatePopulation(Integer population) throws ValidatorException {
        if (population == null) {
            throw new ValidatorException("Население не может быть пустым");
        }
        if (population <= 0) {
            throw new ValidatorException("Население должно быть положительным");
        }
    }

    public void validateEstablishmentDate(String establishmentDate) throws ValidatorException {
        if (establishmentDate == null || establishmentDate.isBlank()) return;
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(establishmentDate, ZONED_DATE_TIME_FORMATTER);
            if (dateTime.isAfter(ZonedDateTime.now())) {
                throw new ValidatorException("Дата основания не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты основания. Ожидается формат yyyy-MM-dd'T'HH:mm:ssXXX");
        }
    }

    public void validateCapital(Boolean capital) throws ValidatorException {
        if (capital == null) {
            throw new ValidatorException("Признак столицы не может быть пустым");
        }
    }

    public void validateMetersAboveSeaLevel(Float metersAboveSeaLevel) throws ValidatorException {
    }

    public void validatePopulationDensity(Float populationDensity) throws ValidatorException {
        if (populationDensity == null) {
            throw new ValidatorException("Плотность населения не может быть пустой");
        }
        if (populationDensity <= 0) {
            throw new ValidatorException("Плотность населения должна быть положительной");
        }
    }

    public void validateClimate(String climate) throws ValidatorException {
    }

    public void validateStandardOfLiving(String standardOfLiving) throws ValidatorException {
        if (standardOfLiving == null || standardOfLiving.isBlank()) {
            throw new ValidatorException("Уровень жизни не может быть пустым");
        }
    }

    public void validateGovernorId(Integer governorId) throws ValidatorException {
        if (governorId == null) {
            throw new ValidatorException("Идентификатор губернатора не может быть пустым");
        }
    }

    public void validateGovernorName(String governorName) throws ValidatorException {
        if (governorName == null || governorName.isBlank()) {
            throw new ValidatorException("Имя губернатора не может быть пустым");
        }
    }

    public void validateGovernorAge(Long governorAge) throws ValidatorException {
        if (governorAge == null) {
            throw new ValidatorException("Возраст губернатора не может быть пустым");
        }
        if (governorAge <= 0) {
            throw new ValidatorException("Возраст губернатора должен быть положительным");
        }
    }

    public void validateGovernorBirthday(String governorBirthday) throws ValidatorException {
        if (governorBirthday == null || governorBirthday.isBlank()) return;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(governorBirthday, DATE_TIME_FORMATTER);
            if (dateTime.isAfter(LocalDateTime.now())) {
                throw new ValidatorException("Дата рождения губернатора не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты рождения губернатора. Ожидается формат yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}
