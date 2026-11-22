package dev.protoskey.mapper;

import dev.protoskey.entity.*;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CityDto;
import dev.protoskey.model.CoordinatesDto;
import dev.protoskey.model.HumanDto;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CityMapper implements Mapper<City, CityDto> {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private final CoordinatesMapper coordinatesMapper = new CoordinatesMapper();
    private final HumanMapper humanMapper = new HumanMapper();

    @Override
    public CityDto parseFrom(City city) throws MapperException {
        if (city == null) {
            return null;
        }
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());

        if (city.getCoordinates() != null) {
            CoordinatesDto coordsDto = coordinatesMapper.parseFrom(city.getCoordinates());
            dto.setCoordinatesId(coordsDto.getId());
            dto.setCoordinatesX(coordsDto.getX());
            dto.setCoordinatesY(coordsDto.getY());
        }

        if (city.getCreationDate() != null) {
            dto.setCreationDate(city.getCreationDate().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        }

        dto.setArea(city.getArea());
        dto.setPopulation(city.getPopulation());

        if (city.getEstablishmentDate() != null) {
            dto.setEstablishmentDate(city.getEstablishmentDate().format(DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT)));
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

        if (city.getGovernor() != null) {
            HumanDto governorDto = humanMapper.parseFrom(city.getGovernor());
            dto.setGovernorId(governorDto.getId());
            dto.setGovernorName(governorDto.getName());
            dto.setGovernorAge(governorDto.getAge());
            dto.setGovernorBirthday(governorDto.getBirthday());
        }

        return dto;
    }

    @Override
    public City parseTo(CityDto dto) throws MapperException {
        if (dto == null) {
            return null;
        }
        City city = new City();
        city.setId(dto.getId());
        city.setName(dto.getName());

        if (dto.getCoordinatesId() != null || dto.getCoordinatesX() != null || dto.getCoordinatesY() != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setId(dto.getCoordinatesId());
            coordinates.setX(dto.getCoordinatesX());
            coordinates.setY(dto.getCoordinatesY());
            city.setCoordinates(coordinates);
        }

        if (dto.getCreationDate() != null && !dto.getCreationDate().isEmpty()) {
            city.setCreationDate(LocalDateTime.parse(dto.getCreationDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        }

        city.setArea(dto.getArea());
        city.setPopulation(dto.getPopulation());

        if (dto.getEstablishmentDate() != null && !dto.getEstablishmentDate().isEmpty()) {
            city.setEstablishmentDate(ZonedDateTime.parse(dto.getEstablishmentDate(), DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT)));
        }

        city.setCapital(dto.getCapital() != null && dto.getCapital());
        city.setMetersAboveSeaLevel(dto.getMetersAboveSeaLevel());
        city.setPopulationDensity(dto.getPopulationDensity());

        if (dto.getClimate() != null && !dto.getClimate().isEmpty()) {
            try {
                city.setClimate(CityMapper.valueOf(Climate.class, dto.getClimate()));
            } catch (IllegalArgumentException e) {
                throw new MapperException("Недопустимое значение климата: " + dto.getClimate());
            }
        }

        if (dto.getStandardOfLiving() != null && !dto.getStandardOfLiving().isEmpty()) {
            try {
                city.setStandardOfLiving(CityMapper.valueOf(StandardOfLiving.class, dto.getStandardOfLiving()));
            } catch (IllegalArgumentException e) {
                throw new MapperException("Недопустимое значение уровня жизни: " + dto.getStandardOfLiving());
            }
        }

        if (dto.getGovernorId() != null || dto.getGovernorName() != null) {
            Human governor = new Human();
            governor.setId(dto.getGovernorId());
            governor.setName(dto.getGovernorName());
            governor.setAge(dto.getGovernorAge());
            city.setGovernor(governor);
        }

        return city;
    }

    private static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        return Enum.valueOf(enumClass, name);
    }
}
