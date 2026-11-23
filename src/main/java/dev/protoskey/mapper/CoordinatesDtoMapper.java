package dev.protoskey.mapper;

import dev.protoskey.entity.Coordinates;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CoordinatesDto;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoordinatesDtoMapper implements Mapper<Coordinates, CoordinatesDto> {
    @Override
    public CoordinatesDto parseFrom(Coordinates coordinates) throws MapperException {
        if (coordinates == null) {
            return null;
        }
        CoordinatesDto dto = new CoordinatesDto();
        dto.setId(coordinates.getId());
        dto.setX(coordinates.getX());
        dto.setY(coordinates.getY());
        return dto;
    }

    @Override
    public Coordinates parseTo(CoordinatesDto dto) throws MapperException {
        if (dto == null) {
            return null;
        }
        Coordinates coordinates = new Coordinates();
        coordinates.setId(dto.getId());
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        return coordinates;
    }
}
