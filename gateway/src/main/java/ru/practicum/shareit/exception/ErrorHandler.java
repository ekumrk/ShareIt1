package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.exception.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUESTD)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}

