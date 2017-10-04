package com.aurea.coverage.parser.idea;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.CoverageParser;

import java.io.InputStream;

public class ArchiveHtmlReportParser implements CoverageParser {
    public ArchiveHtmlReportParser(InputStream archiveInputStream) {

    }

    @Override
    public CoverageIndex buildIndex() {
        return null;
    }
}
