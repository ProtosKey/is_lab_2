package dev.protoskey.point;

import dev.protoskey.entity.Operation;
import dev.protoskey.entity.User;
import dev.protoskey.exception.MapperException;
import dev.protoskey.mapper.OperationDtoMapper;
import dev.protoskey.model.OperationDto;
import dev.protoskey.model.PageDto;
import dev.protoskey.service.OperationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/operations")
@Produces(MediaType.APPLICATION_JSON)
public class OperationResource {
    @Inject
    private OperationService operationService;
    @Inject
    private OperationDtoMapper operationDtoMapper;

    @GET
    public PageDto<OperationDto> history(@QueryParam("offset") @DefaultValue("0") int offset,
                                         @QueryParam("limit") @DefaultValue("10") int limit,
                                         @Context ContainerRequestContext context)
            throws MapperException {
        User user = AuthFilter.currentUser(context);
        List<OperationDto> items = new ArrayList<>();
        for (Operation operation : operationService.historyPageFor(user, offset, limit)) {
            items.add(operationDtoMapper.parseFrom(operation));
        }
        return new PageDto<>(items, operationService.countFor(user));
    }
}
