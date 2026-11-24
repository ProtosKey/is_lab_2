package dev.protoskey.point;

import dev.protoskey.entity.User;
import dev.protoskey.exception.AuthenticationException;
import dev.protoskey.exception.LoginAlreadyTakenException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.CredentialsDto;
import dev.protoskey.service.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    private AuthService authService;
    @Context
    private HttpServletRequest request;

    @POST
    @Path("/register")
    public Response register(CredentialsDto credentials)
            throws LoginAlreadyTakenException, ValidatorException {
        checkCredentials(credentials);
        User user = authService.register(credentials.getLogin(), credentials.getPassword());
        request.getSession().setAttribute(AuthFilter.SESSION_LOGIN, user.getLogin());
        return Response.ok(view(user)).build();
    }

    @POST
    @Path("/login")
    public Response login(CredentialsDto credentials)
            throws AuthenticationException, ValidatorException {
        checkCredentials(credentials);
        User user = authService.login(credentials.getLogin(), credentials.getPassword());
        request.getSession().setAttribute(AuthFilter.SESSION_LOGIN, user.getLogin());
        return Response.ok(view(user)).build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok(Map.of("message", "Выход выполнен")).build();
    }

    @GET
    @Path("/me")
    public Response me(@Context jakarta.ws.rs.container.ContainerRequestContext context) {
        User user = AuthFilter.currentUser(context);
        return Response.ok(view(user)).build();
    }

    private static void checkCredentials(CredentialsDto credentials) throws ValidatorException {
        if (credentials == null
                || credentials.getLogin() == null || credentials.getLogin().isBlank()
                || credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            throw new ValidatorException("Логин и пароль не могут быть пустыми");
        }
    }

    private static Map<String, Object> view(User user) {
        return Map.of(
                "id", user.getId(),
                "login", user.getLogin(),
                "level", user.getUserLevel().name());
    }
}
