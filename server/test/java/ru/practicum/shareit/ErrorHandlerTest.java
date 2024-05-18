package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.handler.ErrorHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleNotFoundException_ReturnsErrorResponseWithHttpStatusNotFound() {
        NotFoundException exception = new NotFoundException("Not found");
        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);
        assertEquals("Not found", response.getError());
    }

    @Test
    void handleThrowable_ReturnsErrorResponseWithHttpStatusInternalServerError() {
        Throwable throwable = new Throwable("Unknown error");

        ErrorHandler.ErrorResponse response = errorHandler.handleThrowable(throwable);
        assertEquals("Unknown error", response.getError());
    }

    public static class ErrorResponse {
        private final HttpStatus httpStatus;
        private final String error;

        public ErrorResponse(HttpStatus httpStatus, String error) {
            this.httpStatus = httpStatus;
            this.error = error;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public String getError() {
            return error;
        }
    }
}