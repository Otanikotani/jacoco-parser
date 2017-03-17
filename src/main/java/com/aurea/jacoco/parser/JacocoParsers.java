package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;

import java.nio.file.Path;

public enum JacocoParsers {
    SINGLETON;

    public static JacocoIndex fromHtml(Path pathToJacocoReport) {
        return new HtmlReportParser(pathToJacocoReport).buildIndex();
    }
}
