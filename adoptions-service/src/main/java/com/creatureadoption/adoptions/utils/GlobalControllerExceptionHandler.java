package com.creatureadoption.adoptions.utils;

import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpErrorInfo> handleNotFoundException(WebRequest request, Exception ex) {
        return createHttpErrorInfoResponseEntity(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<HttpErrorInfo> handleInvalidInputException(WebRequest request, Exception ex) {
        return createHttpErrorInfoResponseEntity(UNPROCESSABLE_ENTITY, request, ex);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AdoptionLimitExceededException.class)
    public ResponseEntity<HttpErrorInfo> handleAdoptionLimitExceededException(WebRequest request, Exception ex) {
        return createHttpErrorInfoResponseEntity(FORBIDDEN, request, ex);
    }

    private ResponseEntity<HttpErrorInfo> createHttpErrorInfoResponseEntity(HttpStatus httpStatus, WebRequest request, Exception ex) {
        final String path = request.getContextPath();
        final String message = ex.getMessage();

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);

        HttpErrorInfo errorInfo = new HttpErrorInfo(httpStatus, path, message);
        return new ResponseEntity<>(errorInfo, httpStatus);
    }
}