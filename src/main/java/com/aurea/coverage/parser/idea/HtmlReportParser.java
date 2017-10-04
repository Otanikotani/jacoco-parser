package com.aurea.coverage.parser.idea;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.CoverageParser;

import java.nio.file.Path;

public class HtmlReportParser implements CoverageParser {
    public HtmlReportParser(Path pathToHtmlReport) {

    }

    @Override
    public CoverageIndex buildIndex() {
        return null;
    }
}
