package dev.protoskey.point;

import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.AuthenticationException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.LoginAlreadyTakenException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.exception.NoCityToMoveException;
import dev.protoskey.exception.NoCityWithClimateException;
import dev.protoskey.exception.PopulationOverflowException;
import dev.protoskey.exception.ReplacementRequiredException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ErrorDto;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.logging.Logger;

@Provider
public class AppExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = Logger.getLogger(AppExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String message = "Внутренняя ошибка сервера";

        if (exception instanceof AuthenticationException) {
            status = Response.Status.UNAUTHORIZED;
            message = exception.getMessage();
        } else if (exception instanceof ValidatorException
                || exception instanceof MapperException
                || exception instanceof JsonbException) {
            status = Response.Status.BAD_REQUEST;
            message = exception.getMessage();
        } else if (exception instanceof EntityNotFoundException
                || exception instanceof NoCityWithClimateException) {
            status = Response.Status.NOT_FOUND;
            message = exception.getMessage();
        } else if (exception instanceof AccessDeniedException) {
            status = Response.Status.FORBIDDEN;
            message = exception.getMessage();
        } else if (exception instanceof LoginAlreadyTakenException
                || exception instanceof UniqueViolationException
                || exception instanceof SameObjectException
                || exception instanceof ReplacementRequiredException
                || exception instanceof NoCityToMoveException
                || exception instanceof PopulationOverflowException) {
            status = Response.Status.CONFLICT;
            message = exception.getMessage();
        } else if (isSerializationFailure(exception)) {
            status = Response.Status.CONFLICT;
            message = "Конфликт параллельной транзакции, повторите операцию";
        } else {
            LOGGER.log(Level.SEVERE, "Необработанное исключение", exception);
        }

        if (message == null) {
            message = status.getReasonPhrase();
        }
        return Response.status(status)
                .entity(new ErrorDto(message))
                .build();
    }

    private static boolean isSerializationFailure(Throwable exception) {
        Deque<Throwable> stack = new ArrayDeque<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        stack.push(exception);
        while (!stack.isEmpty()) {
            Throwable current = stack.pop();
            if (current == null || !seen.add(current)) {
                continue;
            }
            if (current instanceof SQLException sqlEx && "40001".equals(sqlEx.getSQLState())) {
                return true;
            }
            String msg = current.getMessage();
            if (msg != null && (msg.contains("40001") || msg.contains("could not serialize access"))) {
                return true;
            }
            if (current.getCause() != null) {
                stack.push(current.getCause());
            }
            for (Throwable sup : current.getSuppressed()) {
                stack.push(sup);
            }
        }
        return false;
    }
}
