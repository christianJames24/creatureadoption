package com.creatureadoption.creatures.utils.exceptions;

public class DuplicateCreatureNameException extends RuntimeException {

    public DuplicateCreatureNameException() {}

    public DuplicateCreatureNameException(String message) { super(message); }

    public DuplicateCreatureNameException(Throwable cause) { super(cause); }

    public DuplicateCreatureNameException(String message, Throwable cause) { super(message, cause); }
}