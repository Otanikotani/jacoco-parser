package com.aurea.coverage.parser.jacoco;

import com.aurea.coverage.CoverageIndex;

import java.io.InputStream;
import java.nio.file.Path;

public final class JacocoParsers {
    private JacocoParsers() {
    }

    public static CoverageIndex fromXml(Path pathToJacocoReport) {
        return new XmlReportParser(pathToJacocoReport).buildIndex();
    }

    public static CoverageIndex fromXml(InputStream jacocoFileInputStream) {
        return new XmlReportParser(jacocoFileInputStream).buildIndex();
    }

    public static CoverageIndex fromArchive(InputStream archiveInputStream) {
        return new ArchiveReportParser(archiveInputStream).buildIndex();
    }

}
