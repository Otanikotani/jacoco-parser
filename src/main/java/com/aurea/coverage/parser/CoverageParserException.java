package com.aurea.coverage.parser;

public class CoverageParserException extends RuntimeException {
    public CoverageParserException(String message, Object... args) {
        super(String.format(message, args));
    }

    public CoverageParserException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
}
