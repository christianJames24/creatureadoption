package com.creatureadoption.customers.utils;


import com.creatureadoption.customers.utils.exceptions.DeleteRequestBodyNotAllowedException;
import com.creatureadoption.customers.utils.exceptions.DuplicateEmailException;
import com.creatureadoption.customers.utils.exceptions.InvalidInputException;
import com.creatureadoption.customers.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorInfo handleNotFoundException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public HttpErrorInfo handleInvalidInputException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }


    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DuplicateEmailException.class)
    public HttpErrorInfo handleDuplicateEmailException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DeleteRequestBodyNotAllowedException.class)
    public HttpErrorInfo handleDeleteBodyNotAllowed(WebRequest request, Exception ex) {
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }



    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, WebRequest request, Exception ex) {
        final String path = request.getDescription(false);
        // final String path = request.getPath().pathWithinApplication().value();
        final String message = ex.getMessage();
        log.debug("message is: " + message);

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);

        return new HttpErrorInfo(httpStatus, path, message);
    }
}
