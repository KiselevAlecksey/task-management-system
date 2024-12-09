package ru.tms.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.tms.exception.model.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ErrorHandler")
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    public ErrorHandlerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Должен обрабатывать ошибки 'Not Found'")
    void should_handle_not_found_error() {
        String expectedMessage = "404 NOT_FOUND";
        NotFoundException exception = new NotFoundException(expectedMessage);

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertEquals("404 NOT_FOUND", response.getError());
    }

    @Test
    @DisplayName("Должен обрабатывать конфликты параметров")
    void should_handle_parameter_conflict() {
        String parameter = "id";
        String reason = "id must be positive";
        ParameterConflictException exception = new ParameterConflictException(parameter, reason);

        ErrorResponse response = errorHandler.handleParameterConflict(exception);

        assertEquals("Некорректное значение параметра id: id must be positive", response.getError());
    }

    @Test
    @DisplayName("Должен обрабатывать общие ошибки 'Bad Request'")
    void should_handle_generic_bad_request_error() {
        Throwable exception = new Throwable("Произошла непредвиденная ошибка");

        ErrorResponse response = errorHandler.handle(exception);

        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }
}