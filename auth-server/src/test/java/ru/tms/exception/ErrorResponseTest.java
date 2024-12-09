package ru.tms.exception;

import org.junit.jupiter.api.Test;
import ru.tms.exception.model.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ErrorResponseTest {

    @Test
    public void testErrorResponseSingleParameter() {
        String error = "Not Found";

        ErrorResponse errorResponse = new ErrorResponse(error);

        assertEquals(error, errorResponse.getError());
        assertNull(errorResponse.getDescription());
    }

    @Test
    public void testErrorResponseTwoParameters() {
        String error = "Not Found";
        String description = "The requested resource could not be found.";

        ErrorResponse errorResponse = new ErrorResponse(error, description);

        assertEquals(error, errorResponse.getError());
        assertEquals(description, errorResponse.getDescription());
    }
}
