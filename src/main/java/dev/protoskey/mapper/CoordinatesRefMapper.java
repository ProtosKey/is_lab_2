package dev.protoskey.mapper;

import dev.protoskey.entity.Coordinates;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.CoordinatesRef;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoordinatesRefMapper implements Mapper<Coordinates, CoordinatesRef> {
    @Override
    public CoordinatesRef parseFrom(Coordinates coordinates) throws MapperException {
        if (coordinates == null) {
            return null;
        }
        CoordinatesRef ref = new CoordinatesRef();
        ref.setId(coordinates.getId());
        ref.setX(coordinates.getX());
        ref.setY(coordinates.getY());
        return ref;
    }

    @Override
    public Coordinates parseTo(CoordinatesRef ref) throws MapperException {
        if (ref == null) {
            return null;
        }
        Coordinates coordinates = new Coordinates();
        coordinates.setId(ref.getId());
        coordinates.setX(ref.getX());
        coordinates.setY(ref.getY());
        return coordinates;
    }
}
