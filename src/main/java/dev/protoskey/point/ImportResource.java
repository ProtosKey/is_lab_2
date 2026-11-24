package dev.protoskey.point;

import dev.protoskey.entity.OperationType;
import dev.protoskey.entity.User;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportBatchDto;
import dev.protoskey.service.ImportService;
import dev.protoskey.service.OperationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.util.Map;

@Path("/import")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImportResource {
    @Inject
    private ImportService importService;
    @Inject
    private OperationService operationService;

    @POST
    public Response importFile(@QueryParam("fileName") String fileName,
                               InputStream body,
                               @Context ContainerRequestContext context) throws Exception {
        User user = AuthFilter.currentUser(context);
        String name = fileName == null || fileName.isBlank() ? "import.json" : fileName;
        if (body == null) {
            throw new ValidatorException("Файл не передан");
        }

        try {
            ImportBatchDto batch;
            try (Jsonb jsonb = JsonbBuilder.create()) {
                batch = jsonb.fromJson(body, ImportBatchDto.class);
            }
            int imported = importService.importBatch(batch, user, name);
            return Response.ok(Map.of("imported", imported)).build();
        } catch (Exception e) {
            operationService.recordFailure(user, OperationType.IMPORT, name, e.getMessage());
            throw e;
        }
    }
}
