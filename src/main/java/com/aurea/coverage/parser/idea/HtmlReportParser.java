package com.aurea.coverage.parser.idea;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.CoverageParser;
import com.aurea.coverage.parser.CoverageParserException;
import com.aurea.coverage.unit.ModuleCoverage;
import com.aurea.coverage.unit.PackageCoverage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlReportParser implements CoverageParser {
    public static final String INVALID_PATH_ERROR = "Couldn't find idea coverage report in given folder: %s.";
    public static final String NULL_PATH_ERROR = "Given path cannot be null.";
    public static final String PARSE_ERROR = "Failed to parse %s";

    private static final Pattern COVERAGE_CELL_PATTERN = Pattern.compile("([0-9.]+)% \\((\\d+)\\/ (\\d+).+");

    private final Path root;

    public HtmlReportParser(Path pathToHtmlReport) {
        Objects.requireNonNull(pathToHtmlReport, NULL_PATH_ERROR);
        if (!pathToHtmlReport.resolve("index.html").toFile().exists()) {
            throw new CoverageParserException(INVALID_PATH_ERROR, pathToHtmlReport);
        }
        this.root = pathToHtmlReport;
    }

    @Override
    public CoverageIndex buildIndex() {
        ModuleCoverage moduleCoverage = null;
        try {
            moduleCoverage = parseSummary();
        } catch (IOException e) {
            throw new CoverageParserException("Failed to parse %s", root, e);
        }
        return new CoverageIndex(moduleCoverage);
    }

    private ModuleCoverage parseSummary() throws IOException {
        final Path packageSummaryReport = root.resolve("index.html");
        return new ModuleCoverage("NONAME", parsePackages(packageSummaryReport));
    }

    private List<PackageCoverage> parsePackages(final Path packageSummaryReport) {
        try {
            final Document summary = Jsoup.parse(packageSummaryReport.toFile(), StandardCharsets.UTF_8.name());
            final Elements packageRows = summary.select("h2:contains(Coverage Breakdown) + table[class=coverageStats] tr:not(:first-child)");
            return packageRows.stream().map(row -> {
                final Element packageNameCell = row.child(0);
                final String packageFullName = packageNameCell.text();
                final Path pathToPackageReport = root.resolve(packageFullName).resolve("index.html");
                return new PackageCoverage(packageFullName, parseClasses(pathToPackageReport));
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CoverageParserException(PARSE_ERROR, packageSummaryReport, e);
        }
    }

    private List<IdeaClassCoverage> parseClasses(final Path classSummaryReport) {
        try {
            final Document summary = Jsoup.parse(classSummaryReport.toFile(), StandardCharsets.UTF_8.name());
            final Elements classRows = summary.select("br + table[class=coverageStats]:not(:first-child) tr:not(:first-child)");
            return classRows.stream().map(row -> {
                final Element classNameCell = row.child(0);
                final String simpleClassName = classNameCell.text();
                final Element coverageCell = row.child(3);

                Matcher matcher = COVERAGE_CELL_PATTERN.matcher(coverageCell.text());
                if (!matcher.matches()) {
                    throw new CoverageParserException("Failed to parse coverage for class %s. Coverage found %s " +
                            "is not following the pattern %s", simpleClassName, coverageCell.text(), COVERAGE_CELL_PATTERN);
                }
                int covered = Integer.parseInt(matcher.group(2));
                int total = Integer.parseInt(matcher.group(3));
                return new IdeaClassCoverage(simpleClassName, covered, total - covered, total);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new CoverageParserException(PARSE_ERROR, classSummaryReport, e);
        }
    }
}
