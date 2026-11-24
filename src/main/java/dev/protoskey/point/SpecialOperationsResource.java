package dev.protoskey.point;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.User;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.exception.NoCityToMoveException;
import dev.protoskey.exception.NoCityWithClimateException;
import dev.protoskey.exception.PopulationOverflowException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.mapper.CityDtoMapper;
import dev.protoskey.model.CityDto;
import dev.protoskey.service.SpecialOperationsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/special")
@Produces(MediaType.APPLICATION_JSON)
public class SpecialOperationsResource {
    @Inject
    private SpecialOperationsService specialOperationsService;
    @Inject
    private CityDtoMapper cityDtoMapper;

    @DELETE
    @Path("/cities/climate/{climate}")
    public CityDto deleteOneByClimate(@PathParam("climate") String climate,
                                      @Context ContainerRequestContext context)
            throws AccessDeniedException, NoCityWithClimateException, MapperException, ValidatorException {
        User user = AuthFilter.currentUser(context);
        City deleted = specialOperationsService.deleteOneByClimate(parseClimate(climate), user);
        return cityDtoMapper.parseFrom(deleted);
    }

    @GET
    @Path("/cities/count-by-meters")
    public Map<String, Long> countByMetersAboveSeaLevel(@QueryParam("value") Float value)
            throws ValidatorException {
        if (value == null) {
            throw new ValidatorException("Не указано значение высоты над уровнем моря");
        }
        return Map.of("count", specialOperationsService.countByMetersAboveSeaLevel(value));
    }

    @GET
    @Path("/cities/by-name")
    public List<CityDto> findByNameContains(@QueryParam("substring") String substring)
            throws MapperException, ValidatorException {
        if (substring == null || substring.isBlank()) {
            throw new ValidatorException("Не указана подстрока для поиска");
        }
        List<CityDto> items = new ArrayList<>();
        for (City city : specialOperationsService.findByNameContains(substring)) {
            items.add(cityDtoMapper.parseFrom(city));
        }
        return items;
    }

    @POST
    @Path("/cities/move-population")
    public CityDto movePopulation(@QueryParam("source") Integer sourceId,
                                  @QueryParam("target") Integer targetId,
                                  @Context ContainerRequestContext context)
            throws AccessDeniedException, SameObjectException, EntityNotFoundException,
            PopulationOverflowException, MapperException {
        User user = AuthFilter.currentUser(context);
        City target = specialOperationsService.movePopulation(sourceId, targetId, user);
        return cityDtoMapper.parseFrom(target);
    }

    @POST
    @Path("/cities/move-population-to-smallest")
    public CityDto movePopulationToSmallest(@QueryParam("source") Integer sourceId,
                                            @Context ContainerRequestContext context)
            throws AccessDeniedException, EntityNotFoundException, NoCityToMoveException,
            PopulationOverflowException, MapperException {
        User user = AuthFilter.currentUser(context);
        City target = specialOperationsService.movePopulationToSmallest(sourceId, user);
        return cityDtoMapper.parseFrom(target);
    }

    private static Climate parseClimate(String climate) throws ValidatorException {
        try {
            return Climate.valueOf(climate);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidatorException("Недопустимое значение климата: " + climate);
        }
    }
}
