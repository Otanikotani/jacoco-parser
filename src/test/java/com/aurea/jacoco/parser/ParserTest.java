package com.aurea.jacoco.parser;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ParserTest {

    protected Path getPathToTestReport() throws URISyntaxException {
        URL jacoco = getClass().getResource("../report");
        return Paths.get(jacoco.toURI());
    }
}
