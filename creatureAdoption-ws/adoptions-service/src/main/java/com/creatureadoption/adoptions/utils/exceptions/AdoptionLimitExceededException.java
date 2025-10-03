package com.creatureadoption.adoptions.utils.exceptions;

public class AdoptionLimitExceededException extends RuntimeException {
    public AdoptionLimitExceededException(String message) {
        super(message);
    }
}