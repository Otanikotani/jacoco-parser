package com.aurea.jacoco.parser;

public class JacocoParserException extends RuntimeException {
    public JacocoParserException(String message, Object... args) {
        super(String.format(message, args));
    }

    public JacocoParserException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
}
