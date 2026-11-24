package dev.protoskey.point;

import dev.protoskey.entity.Human;
import dev.protoskey.entity.User;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.exception.ReplacementRequiredException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.mapper.HumanDtoMapper;
import dev.protoskey.model.HumanDto;
import dev.protoskey.model.HumanRequest;
import dev.protoskey.model.PageDto;
import dev.protoskey.service.HumanService;
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

@Path("/governors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanResource {
    @Inject
    private HumanService humanService;

    @Inject
    private HumanDtoMapper humanDtoMapper;

    @GET
    public PageDto<HumanDto> findPage(@QueryParam("offset") @DefaultValue("0") int offset,
                                      @QueryParam("limit") @DefaultValue("10") int limit)
            throws MapperException {
        List<HumanDto> items = new ArrayList<>();
        for (Human human : humanService.findPage(offset, limit)) {
            items.add(humanDtoMapper.parseFrom(human));
        }
        return new PageDto<>(items, humanService.count());
    }

    @GET
    @Path("/{id}")
    public HumanDto findById(@PathParam("id") Integer id)
            throws EntityNotFoundException, MapperException {
        return humanDtoMapper.parseFrom(humanService.find(id));
    }

    @GET
    @Path("/{id}/usage")
    public java.util.Map<String, Integer> usage(@PathParam("id") Integer id)
            throws EntityNotFoundException {
        return java.util.Map.of("count", humanService.countLinkedCities(id));
    }

    @POST
    public HumanDto create(HumanRequest request, @Context ContainerRequestContext context)
            throws ValidatorException, UniqueViolationException, MapperException {
        User user = AuthFilter.currentUser(context);
        return humanDtoMapper.parseFrom(humanService.create(request, user));
    }

    @PUT
    @Path("/{id}")
    public HumanDto update(@PathParam("id") Integer id, HumanRequest request,
                           @Context ContainerRequestContext context)
            throws EntityNotFoundException, AccessDeniedException, ValidatorException,
            UniqueViolationException, MapperException {
        User user = AuthFilter.currentUser(context);
        return humanDtoMapper.parseFrom(humanService.update(id, request, user));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id,
                           @QueryParam("replacement") Integer replacementId,
                           @Context ContainerRequestContext context)
            throws EntityNotFoundException, AccessDeniedException, ReplacementRequiredException,
            SameObjectException {
        User user = AuthFilter.currentUser(context);
        humanService.delete(id, user, replacementId);
        return Response.noContent().build();
    }
}
