package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;

import java.nio.file.Path;

public final class JacocoParsers {
    private JacocoParsers() {}

    public static JacocoIndex fromXml(Path pathToJacocoReport) {
        return new XmlReportParser(pathToJacocoReport).buildIndex();
    }
}
