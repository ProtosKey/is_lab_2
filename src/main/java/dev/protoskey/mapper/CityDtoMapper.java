package dev.protoskey.mapper;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.StandardOfLiving;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CityDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@ApplicationScoped
public class CityDtoMapper implements Mapper<City, CityDto> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Inject
    private CoordinatesDtoMapper coordinatesMapper;
    @Inject
    private HumanDtoMapper humanMapper;

    @Override
    public CityDto parseFrom(City city) throws MapperException {
        if (city == null) {
            return null;
        }
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setCoordinates(coordinatesMapper.parseFrom(city.getCoordinates()));
        if (city.getCreationDate() != null) {
            dto.setCreationDate(city.getCreationDate().format(DATE_FORMATTER));
        }
        dto.setArea(city.getArea());
        dto.setPopulation(city.getPopulation());
        if (city.getEstablishmentDate() != null) {
            dto.setEstablishmentDate(city.getEstablishmentDate().format(DATE_FORMATTER));
        }
        dto.setCapital(city.isCapital());
        dto.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        dto.setPopulationDensity(city.getPopulationDensity());
        if (city.getClimate() != null) {
            dto.setClimate(city.getClimate().name());
        }
        if (city.getStandardOfLiving() != null) {
            dto.setStandardOfLiving(city.getStandardOfLiving().name());
        }
        dto.setGovernor(humanMapper.parseFrom(city.getGovernor()));
        return dto;
    }

    @Override
    public City parseTo(CityDto dto) throws MapperException {
        if (dto == null) {
            return null;
        }
        City city = new City();
        city.setName(dto.getName());
        city.setCoordinates(coordinatesMapper.parseTo(dto.getCoordinates()));
        if (dto.getCreationDate() != null && !dto.getCreationDate().isEmpty()) {
            city.setCreationDate(parseCreationDate(dto.getCreationDate()));
        }
        city.setArea(dto.getArea());
        city.setPopulation(dto.getPopulation());
        if (dto.getEstablishmentDate() != null && !dto.getEstablishmentDate().isEmpty()) {
            city.setEstablishmentDate(parseEstablishmentDate(dto.getEstablishmentDate()));
        }
        city.setCapital(dto.getCapital() != null && dto.getCapital());
        city.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        city.setPopulationDensity(dto.getPopulationDensity());
        city.setClimate(parseClimate(dto.getClimate()));
        city.setStandardOfLiving(parseStandardOfLiving(dto.getStandardOfLiving()));
        city.setGovernor(humanMapper.parseTo(dto.getGovernor()));
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
