package com.aurea.coverage.parser;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.idea.HtmlReportParser;

import java.nio.file.Path;

public final class IdeaParsers {
    private IdeaParsers() {
    }

    public static CoverageIndex fromHtml(Path pathToHtmlReport) {
        return new HtmlReportParser(pathToHtmlReport).buildIndex();
    }
}
