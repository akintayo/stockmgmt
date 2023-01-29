package com.rbc.stockticker.web;

import com.rbc.stockticker.exception.DuplicateObjectException;
import com.rbc.stockticker.exception.ErrorDTO;
import com.rbc.stockticker.exception.InvalidFieldException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
public class StockControllerExceptionHandler {
    private static final String INVALID_REQUEST = "invalid-request";
    private static final String MAX_SIZE_EXCEEDED = "max-size-exceeded";

    private static final String DUPLICATE_ENTRY = "conflicting-state";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDTO handleInvalidException(ConstraintViolationException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String[] paths = error.getPropertyPath().toString().split("\\.");
            String fieldName = paths[paths.length - 1];
            String errorMessage = error.getMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        ErrorDTO errors = new ErrorDTO();
        errors.setCode(INVALID_REQUEST);
        errors.setFieldErrors(validationErrors);
        return errors;
    }


    @ResponseStatus(CONFLICT)
    @ExceptionHandler({DuplicateObjectException.class})
    @ResponseBody
    public ErrorDTO handleDuplicate(DuplicateObjectException ex) {
        ErrorDTO error = new ErrorDTO();
        error.setMessage(ex.getMessage());
        error.setCode(DUPLICATE_ENTRY);
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidFieldException.class})
    @ResponseBody
    public ErrorDTO invalidField(InvalidFieldException ex) {
        ErrorDTO error = new ErrorDTO();
        error.setMessage(ex.getMessage());
        error.setCode(INVALID_REQUEST);
        return error;
    }

    @ExceptionHandler({MultipartException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorDTO multipartExceptionHandler(MultipartException e) {
        Throwable th = e.getCause();
        ErrorDTO error = new ErrorDTO();
        if (th instanceof IllegalStateException) {
            Throwable cause = th.getCause();
            if (cause instanceof MaxUploadSizeExceededException ex) {
                error.setMessage(ex.getMessage());
                error.setCode(MAX_SIZE_EXCEEDED);
                return error;
            }
        }

        error.setMessage(e.getMessage());
        error.setCode(MAX_SIZE_EXCEEDED);
        return error;
    }
    @ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(CONFLICT)
    public ErrorDTO handleConflict(org.hibernate.exception.ConstraintViolationException exception) {
        ErrorDTO errorDTO = new ErrorDTO();
        String message;
        errorDTO.setCode(DUPLICATE_ENTRY);
        message = exception.getSQLException().getMessage().split("=")[1].replace("(", "").replace(")", "");
        errorDTO.setMessage(message);
        return errorDTO;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDTO handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        ErrorDTO errors = new ErrorDTO();
        errors.setCode(INVALID_REQUEST);
        errors.setFieldErrors(validationErrors);
        errors.setMessage("Please check message and try again");
        return errors;
    }
}
