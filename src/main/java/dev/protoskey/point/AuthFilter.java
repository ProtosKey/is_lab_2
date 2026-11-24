package dev.protoskey.point;

import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.service.AuthService;
import dev.protoskey.entity.User;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import dev.protoskey.model.ErrorDto;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
    public static final String SESSION_LOGIN = "login";
    public static final String CURRENT_USER = "currentUser";
    @Inject
    private AuthService authService;
    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext context) {
        String path = normalize(context.getUriInfo().getPath());
        if (path.equals("auth/login") || path.equals("auth/register")) {
            return;
        }

        HttpSession session = servletRequest.getSession(false);
        String login = session != null ? (String) session.getAttribute(SESSION_LOGIN) : null;
        if (login == null) {
            abort(context, "Требуется авторизация");
            return;
        }

        try {
            User user = authService.findByLogin(login);
            context.setProperty(CURRENT_USER, user);
        } catch (EntityNotFoundException e) {
            abort(context, "Сессия недействительна");
        }
    }

    private void abort(ContainerRequestContext context, String message) {
        context.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorDto(message))
                .build());
    }

    private static String normalize(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    public static User currentUser(ContainerRequestContext context) {
        return (User) context.getProperty(CURRENT_USER);
    }
}
