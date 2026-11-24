package dev.protoskey.point;

import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.LevelRequest;
import dev.protoskey.model.PasswordRequest;
import dev.protoskey.model.UserDto;
import dev.protoskey.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {
    @Inject
    private UserService userService;

    @GET
    public List<UserDto> findAll(@Context ContainerRequestContext context)
            throws AccessDeniedException {
        User current = AuthFilter.currentUser(context);
        List<UserDto> items = new ArrayList<>();
        for (User user : userService.findAll(current)) {
            items.add(new UserDto(user.getId(), user.getLogin(), user.getUserLevel().name()));
        }
        return items;
    }

    @PUT
    @Path("/{id}/level")
    public UserDto changeLevel(@PathParam("id") Integer id, LevelRequest body,
                               @Context ContainerRequestContext context)
            throws AccessDeniedException, EntityNotFoundException, ValidatorException {
        User current = AuthFilter.currentUser(context);
        UserLevel level = parseLevel(body == null ? null : body.getLevel());
        User updated = userService.changeLevel(id, level, current);
        return new UserDto(updated.getId(), updated.getLogin(), updated.getUserLevel().name());
    }

    @PUT
    @Path("/{id}/password")
    public Response changePassword(@PathParam("id") Integer id, PasswordRequest body,
                                   @Context ContainerRequestContext context)
            throws AccessDeniedException, EntityNotFoundException, ValidatorException {
        User current = AuthFilter.currentUser(context);
        if (body == null) {
            throw new ValidatorException("Не переданы пароли");
        }
        userService.changePassword(id, body.getOldPassword(), body.getNewPassword(), current);
        return Response.ok(java.util.Map.of("message", "Пароль изменён")).build();
    }

    private static UserLevel parseLevel(String level) throws ValidatorException {
        try {
            return UserLevel.valueOf(level);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidatorException("Недопустимый уровень доступа: " + level + ". Допустимо: ADMIN, USER");
        }
    }
}
