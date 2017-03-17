package com.aurea.jacoco;

public class JacocoParserException extends RuntimeException {
    public JacocoParserException(String message) {
        super(message);
    }

    public JacocoParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
