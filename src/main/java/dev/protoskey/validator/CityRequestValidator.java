package dev.protoskey.validator;

import dev.protoskey.entity.Climate;
import dev.protoskey.entity.StandardOfLiving;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CityRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

@ApplicationScoped
public class CityRequestValidator implements Validator<CityRequest> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Inject
    private CoordinatesRefValidator coordinatesValidator;
    @Inject
    private HumanRefValidator humanValidator;

    @Override
    public void validate(CityRequest request) throws ValidatorException {
        if (request == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        validateName(request.getName());
        validateArea(request.getArea());
        validatePopulation(request.getPopulation());
        validateEstablishmentDate(request.getEstablishmentDate());
        validateCapital(request.getCapital());
        validateMetersAboveSeaLevel(request.getMetersAboveSeaLevel());
        validatePopulationDensity(request.getPopulationDensity());
        validateClimate(request.getClimate());
        validateStandardOfLiving(request.getStandardOfLiving());
        coordinatesValidator.validate(request.getCoordinates());
        humanValidator.validate(request.getGovernor());
    }

    public void validateName(String name) throws ValidatorException {
        if (name == null || name.isBlank()) {
            throw new ValidatorException("Название города не может быть пустым");
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
            LocalDate date = LocalDate.parse(establishmentDate, FORMATTER);
            if (date.isAfter(LocalDate.now())) {
                throw new ValidatorException("Дата основания не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты основания. Ожидается формат yyyy-MM-dd");
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
        if (climate == null || climate.isBlank()) return; // Поле может быть null
        try {
            Climate.valueOf(climate);
        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(Climate.values()).map(Enum::name).collect(Collectors.joining(", "));
            throw new ValidatorException("Недопустимое значение климата: " + climate + ". Допустимые значения: " + allowed);
        }
    }

    public void validateStandardOfLiving(String standardOfLiving) throws ValidatorException {
        if (standardOfLiving == null || standardOfLiving.isBlank()) {
            throw new ValidatorException("Уровень жизни не может быть пустым");
        }
        try {
            StandardOfLiving.valueOf(standardOfLiving);
        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(StandardOfLiving.values()).map(Enum::name).collect(Collectors.joining(", "));
            throw new ValidatorException("Недопустимое значение уровня жизни: " + standardOfLiving + ". Допустимые значения: " + allowed);
        }
    }
}
