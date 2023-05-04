package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.exception.CategoryException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse notFound(final DataIntegrityViolationException e) {
        log.warn("Conflict duplicate {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse notFound(final ConflictException e) {
        log.warn("Conflict {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noContent(final CategoryException e) {
        log.warn("No content {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NO_CONTENT.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse resolve(final HttpMessageNotReadableException e) {
        log.warn("No content {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT.getReasonPhrase());
    }
}
