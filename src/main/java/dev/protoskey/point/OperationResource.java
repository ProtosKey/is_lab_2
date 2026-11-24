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
    public PageDto<OperationDto> history(@QueryParam("cursor") Integer cursor,
                                         @QueryParam("limit") @DefaultValue("10") int limit,
                                         @Context ContainerRequestContext context)
            throws MapperException {
        User user = AuthFilter.currentUser(context);
        int pageSize = Math.max(1, limit);
        List<Operation> rows = operationService.historyPageFor(user, cursor, pageSize);
        boolean hasMore = rows.size() > pageSize;
        List<Operation> pageRows = hasMore ? rows.subList(0, pageSize) : rows;
        List<OperationDto> items = new ArrayList<>();
        for (Operation operation : pageRows) {
            items.add(operationDtoMapper.parseFrom(operation));
        }
        String nextCursor = hasMore && !items.isEmpty() ? String.valueOf(items.get(items.size() - 1).getId()) : null;
        return new PageDto<>(items, operationService.countFor(user), nextCursor, hasMore);
    }
}
