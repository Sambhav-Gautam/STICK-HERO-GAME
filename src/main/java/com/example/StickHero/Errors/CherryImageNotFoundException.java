package com.example.StickHero.Errors;

/**
 * Exception thrown when the cherry image is not found.
 * This exception can be used to handle cases where the cherry image file is missing or inaccessible.
 */
public class CherryImageNotFoundException extends Throwable {

    /**
     * Constructs a new CherryImageNotFoundException with the specified detail message.
     *
     * @param message the detail message. This message is saved for later retrieval by the getMessage() method.
     */
    public CherryImageNotFoundException(String message) {
        super(message);
    }
}
