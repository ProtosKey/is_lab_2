package dev.protoskey.point;

import dev.protoskey.entity.City;
import dev.protoskey.entity.User;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.mapper.CityDtoMapper;
import dev.protoskey.model.CityDto;
import dev.protoskey.model.CityRequest;
import dev.protoskey.model.PageDto;
import dev.protoskey.repository.CityQuery;
import dev.protoskey.service.CityService;
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
import java.util.ArrayList;
import java.util.List;

@Path("/cities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CityResource {
    @Inject
    private CityService cityService;
    @Inject
    private CityDtoMapper cityDtoMapper;

    @GET
    public PageDto<CityDto> findPage(@QueryParam("filterField") String filterField,
                                     @QueryParam("filterValue") String filterValue,
                                     @QueryParam("sortField") String sortField,
                                     @QueryParam("ascending") @DefaultValue("true") boolean ascending,
                                     @QueryParam("offset") @DefaultValue("0") int offset,
                                     @QueryParam("limit") @DefaultValue("10") int limit)
            throws MapperException {
        CityQuery query = new CityQuery(filterField, filterValue, sortField, ascending, offset, limit);
        List<CityDto> items = new ArrayList<>();
        for (City city : cityService.findPage(query)) {
            items.add(cityDtoMapper.parseFrom(city));
        }
        return new PageDto<>(items, cityService.count(query));
    }

    @GET
    @Path("/{id}")
    public CityDto findById(@PathParam("id") Integer id) throws EntityNotFoundException, MapperException {
        return cityDtoMapper.parseFrom(cityService.find(id));
    }

    @POST
    public Response create(CityRequest request, @Context ContainerRequestContext context)
            throws ValidatorException, EntityNotFoundException, MapperException {
        User user = AuthFilter.currentUser(context);
        City city = cityService.create(request, user);
        return Response.status(Response.Status.CREATED)
                .entity(cityDtoMapper.parseFrom(city))
                .build();
    }

    @PUT
    @Path("/{id}")
    public CityDto update(@PathParam("id") Integer id, CityRequest request,
                          @Context ContainerRequestContext context)
            throws ValidatorException, EntityNotFoundException, AccessDeniedException, MapperException {
        User user = AuthFilter.currentUser(context);
        return cityDtoMapper.parseFrom(cityService.update(id, request, user));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id, @Context ContainerRequestContext context)
            throws EntityNotFoundException, AccessDeniedException {
        User user = AuthFilter.currentUser(context);
        cityService.delete(id, user);
        return Response.noContent().build();
    }
}
