package dev.protoskey.mapper;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.StandardOfLiving;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CityRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@ApplicationScoped
public class CityRequestMapper implements Mapper<City, CityRequest> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private CoordinatesRefMapper coordinatesMapper;
    @Inject
    private HumanRefMapper humanMapper;

    @Override
    public CityRequest parseFrom(City city) throws MapperException {
        if (city == null) {
            return null;
        }
        CityRequest request = new CityRequest();
        request.setName(city.getName());
        request.setCoordinates(coordinatesMapper.parseFrom(city.getCoordinates()));
        if (city.getCreationDate() != null) {
            request.setCreationDate(city.getCreationDate().format(DATE_FORMATTER));
        }
        request.setArea(city.getArea());
        request.setPopulation(city.getPopulation());
        if (city.getEstablishmentDate() != null) {
            request.setEstablishmentDate(city.getEstablishmentDate().format(DATE_FORMATTER));
        }
        request.setCapital(city.isCapital());
        request.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        request.setPopulationDensity(city.getPopulationDensity());
        if (city.getClimate() != null) {
            request.setClimate(city.getClimate().name());
        }
        if (city.getStandardOfLiving() != null) {
            request.setStandardOfLiving(city.getStandardOfLiving().name());
        }
        request.setGovernor(humanMapper.parseFrom(city.getGovernor()));
        return request;
    }

    @Override
    public City parseTo(CityRequest request) throws MapperException {
        if (request == null) {
            return null;
        }
        City city = new City();
        city.setName(request.getName());
        city.setCoordinates(coordinatesMapper.parseTo(request.getCoordinates()));
        if (request.getCreationDate() != null && !request.getCreationDate().isEmpty()) {
            city.setCreationDate(parseCreationDate(request.getCreationDate()));
        }
        city.setArea(request.getArea());
        city.setPopulation(request.getPopulation());
        if (request.getEstablishmentDate() != null && !request.getEstablishmentDate().isEmpty()) {
            city.setEstablishmentDate(parseEstablishmentDate(request.getEstablishmentDate()));
        }
        city.setCapital(request.getCapital() != null && request.getCapital());
        city.setMetersAboveSeaLevel(request.getMetersAboveSeaLevel());
        city.setPopulationDensity(request.getPopulationDensity());
        city.setClimate(parseClimate(request.getClimate()));
        city.setStandardOfLiving(parseStandardOfLiving(request.getStandardOfLiving()));
        city.setGovernor(humanMapper.parseTo(request.getGovernor()));
        return city;
    }

    private LocalDateTime parseCreationDate(String creationDate) throws MapperException {
        try {
            return LocalDate.parse(creationDate, DATE_FORMATTER).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new MapperException("Неверный формат даты создания: " + creationDate);
        }
    }

    private ZonedDateTime parseEstablishmentDate(String establishmentDate) throws MapperException {
        try {
            return LocalDate.parse(establishmentDate, DATE_FORMATTER).atStartOfDay(ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            throw new MapperException("Неверный формат даты основания: " + establishmentDate);
        }
    }

    private Climate parseClimate(String climate) throws MapperException {
        if (climate == null || climate.isEmpty()) {
            return null;
        }
        try {
            return Climate.valueOf(climate);
        } catch (IllegalArgumentException e) {
            throw new MapperException("Недопустимое значение климата: " + climate);
        }
    }

    private StandardOfLiving parseStandardOfLiving(String standardOfLiving) throws MapperException {
        if (standardOfLiving == null || standardOfLiving.isEmpty()) {
            throw new MapperException("Уровень жизни не может быть пустым");
        }
        try {
            return StandardOfLiving.valueOf(standardOfLiving);
        } catch (IllegalArgumentException e) {
            throw new MapperException("Недопустимое значение уровня жизни: " + standardOfLiving);
        }
    }
}
