package com.leliuk.exception;

public class UnificationException extends RuntimeException {
    private static final String MESSAGE = "Couldn't unify from %s to %s";
    private static final String MESSAGE_WITH_ADDITIONAL = "Couldn't unify from %s to %s (%s)";

    public UnificationException(Object from, Object to) {
        super(String.format(MESSAGE, from, to));
    }

    public UnificationException(Object from, Object to, String additionalInfo) {
        super(String.format(MESSAGE_WITH_ADDITIONAL, from, to, additionalInfo));
    }
}
