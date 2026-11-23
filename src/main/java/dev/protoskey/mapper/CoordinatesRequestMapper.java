package dev.protoskey.mapper;

import dev.protoskey.entity.Coordinates;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CoordinatesRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoordinatesRequestMapper implements Mapper<Coordinates, CoordinatesRequest> {
    @Override
    public CoordinatesRequest parseFrom(Coordinates coordinates) throws MapperException {
        if (coordinates == null) {
            return null;
        }
        CoordinatesRequest request = new CoordinatesRequest();
        request.setX(coordinates.getX());
        request.setY(coordinates.getY());
        return request;
    }

    @Override
    public Coordinates parseTo(CoordinatesRequest request) throws MapperException {
        if (request == null) {
            return null;
        }
        Coordinates coordinates = new Coordinates();
        coordinates.setX(request.getX());
        coordinates.setY(request.getY());
        return coordinates;
    }
}
