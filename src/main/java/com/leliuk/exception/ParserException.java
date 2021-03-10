package com.leliuk.exception;

public class ParserException extends RuntimeException {
    private static final String MESSAGE = "Couldn't parse %s from '%s'";
    private static final String MESSAGE_WITH_ADDITIONAL = "Couldn't parse %s from '%s' (%s)";
    private static final String MESSAGE_WITH_CAUSES = "Couldn't parse %s from '%s' (%s)";

    public ParserException(Class<?> parseType, String source) {
        super(String.format(MESSAGE, parseType, source));
    }

    public ParserException(Class<?> parseType, String source, String additional) {
        super(String.format(MESSAGE_WITH_ADDITIONAL, parseType, source, additional));
    }

    public ParserException(Class<?> parseType, String source, Throwable cause) {
        super(String.format(MESSAGE, parseType, source), cause);
    }
}
