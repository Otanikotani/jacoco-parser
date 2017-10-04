package com.aurea.coverage.parser;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.idea.ArchiveHtmlReportParser;
import com.aurea.coverage.parser.idea.HtmlReportParser;

import java.io.InputStream;
import java.nio.file.Path;

public final class IdeaParsers {
    private IdeaParsers() {
    }

    public static CoverageIndex fromHtml(Path pathToHtmlReport) {
        return new HtmlReportParser(pathToHtmlReport).buildIndex();
    }

    public static CoverageIndex fromArchive(InputStream archiveInputStream) {
        return new ArchiveHtmlReportParser(archiveInputStream).buildIndex();
    }

}
