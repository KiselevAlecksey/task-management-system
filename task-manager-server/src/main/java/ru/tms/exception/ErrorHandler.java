package ru.tms.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tms.exception.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.info("Получен статус 404 Not found {}", e.getMessage(), e);

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleParameterConflict(final ParameterConflictException e) {
        log.info("Received status 409 Conflict {}", e.getMessage(), e);

        return new ErrorResponse("Некорректное значение параметра " + e.getParameter() + ": " + e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {
        log.trace("Получен статус 500 Internal server error {}", e.getMessage(), e);

        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
        log.trace("Получен статус 400 Bad request {}", e.getMessage(), e);

        return new ErrorResponse("Некорректное значение параметра " + e.getParameter() + ": " + e.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConditionsNotMetException(final ConditionsNotMetException e) {
        log.trace("Получен статус 400 Bad request {}", e.getMessage(), e);

        return new ErrorResponse("Условия не соблюдены: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.info("Получен статус 400 Bad request {}", e.getMessage(), e);

        String message = null;

        FieldError fieldError = e.getBindingResult().getFieldError();

        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }

        return new ErrorResponse("Некорректное значение параметра "
                + e.getParameter().getParameterName() + ": ", message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(final ConstraintViolationException e) {
        log.info("Получен статус 400 Bad request {}", e.getMessage(), e);

        return new ErrorResponse("Некорректное значение параметра: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
        log.info("Получен статус 400 Bad request {}", e.getMessage(), e);

        return new ErrorResponse("Некорректное значение параметра: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(
            final AccessDeniedException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>("Доступ запрещен", HttpStatus.FORBIDDEN);
    }
}
