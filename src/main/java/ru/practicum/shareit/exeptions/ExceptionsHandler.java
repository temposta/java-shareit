package ru.practicum.shareit.exeptions;

import jakarta.validation.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Общий контроллер обработки исключений приложения
 */
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("{%s : %s}", error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(errors.toString()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorMessage> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ResourceUnavailableException.class)
    public ResponseEntity<ErrorMessage> handleResourceUnavailableException(ResourceUnavailableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage("Непредвиденная ошибка сервера"));
    }
}
