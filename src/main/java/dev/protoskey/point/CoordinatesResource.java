package dev.protoskey.point;

import dev.protoskey.entity.Coordinates;
import dev.protoskey.entity.User;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.exception.ReplacementRequiredException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.mapper.CoordinatesDtoMapper;
import dev.protoskey.model.CoordinatesDto;
import dev.protoskey.model.CoordinatesRequest;
import dev.protoskey.model.PageDto;
import dev.protoskey.service.CoordinatesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesResource {
    @Inject
    private CoordinatesService coordinatesService;

    @Inject
    private CoordinatesDtoMapper coordinatesDtoMapper;

    @GET
    public PageDto<CoordinatesDto> findPage(@QueryParam("cursor") Integer cursor,
                                            @QueryParam("limit") @DefaultValue("10") int limit)
            throws MapperException {
        int pageSize = Math.max(1, limit);
        List<Coordinates> rows = coordinatesService.findPage(cursor, pageSize);
        boolean hasMore = rows.size() > pageSize;
        List<Coordinates> pageRows = hasMore ? rows.subList(0, pageSize) : rows;
        List<CoordinatesDto> items = new java.util.ArrayList<>();
        for (Coordinates coordinates : pageRows) {
            items.add(coordinatesDtoMapper.parseFrom(coordinates));
        }
        String nextCursor = hasMore && !items.isEmpty() ? String.valueOf(items.get(items.size() - 1).getId()) : null;
        return new PageDto<>(items, coordinatesService.count(), nextCursor, hasMore);
    }

    @GET
    @Path("/{id}")
    public CoordinatesDto findById(@PathParam("id") Integer id)
            throws EntityNotFoundException, MapperException {
        return coordinatesDtoMapper.parseFrom(coordinatesService.find(id));
    }

    @GET
    @Path("/{id}/usage")
    public java.util.Map<String, Integer> usage(@PathParam("id") Integer id)
            throws EntityNotFoundException {
        return java.util.Map.of("count", coordinatesService.countLinkedCities(id));
    }

    @POST
    public CoordinatesDto create(CoordinatesRequest request, @Context ContainerRequestContext context)
            throws ValidatorException, UniqueViolationException, MapperException {
        User user = AuthFilter.currentUser(context);
        return coordinatesDtoMapper.parseFrom(coordinatesService.create(request, user));
    }

    @PUT
    @Path("/{id}")
    public CoordinatesDto update(@PathParam("id") Integer id, CoordinatesRequest request,
                                 @Context ContainerRequestContext context)
            throws EntityNotFoundException, AccessDeniedException, ValidatorException,
            UniqueViolationException, MapperException {
        User user = AuthFilter.currentUser(context);
        return coordinatesDtoMapper.parseFrom(coordinatesService.update(id, request, user));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id,
                           @QueryParam("replacement") Integer replacementId,
                           @Context ContainerRequestContext context)
            throws EntityNotFoundException, AccessDeniedException, ReplacementRequiredException,
            SameObjectException {
        User user = AuthFilter.currentUser(context);
        coordinatesService.delete(id, user, replacementId);
        return Response.noContent().build();
    }
}
