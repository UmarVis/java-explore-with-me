package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse resolve(final NotFoundException e) {
        log.warn("No found {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse requestParameterException(final MissingServletRequestParameterException e) {
        log.warn("Valid {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse valid(final MethodArgumentNotValidException e) {
        log.warn("Valid {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse error(final Throwable e) {
//        log.warn("Error {}", e.getMessage());
//        return new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//    }
}
