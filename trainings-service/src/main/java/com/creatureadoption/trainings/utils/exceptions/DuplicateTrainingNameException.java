package com.creatureadoption.trainings.utils.exceptions;

public class DuplicateTrainingNameException extends RuntimeException {

    public DuplicateTrainingNameException() {}

    public DuplicateTrainingNameException(String message) { super(message); }

    public DuplicateTrainingNameException(Throwable cause) { super(cause); }

    public DuplicateTrainingNameException(String message, Throwable cause) { super(message, cause); }
}