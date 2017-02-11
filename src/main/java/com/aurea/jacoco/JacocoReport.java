package com.aurea.jacoco;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class JacocoReport {

    private static final Logger logger = LogManager.getLogger(JacocoReport.class.getSimpleName());

    private static final Map<ClassCoverage, List<Range>> EMPTY_LINES_RESULT = Collections.emptyMap();
    private static final Map<ClassCoverage, Set<MethodCoverage>> EMPTY_METHODS_RESULT = Collections.emptyMap();
    private static final int NUMBER_OF_LINES_COLUMN = 8;
    private static final int NUMBER_OF_MISSING_LINES_COLUMN = 7;
    private static final String CHARSET = "UTF-8";

    private Path pathToJacocoReport;
    private ReportStats reportStats;

    private final Supplier<Map<ClassCoverage, List<Range>>> rangesCache = Suppliers.memoize(() -> {
                try {
                    Map<ClassCoverage, List<Range>> coveredRanges = new ConcurrentHashMap<>();
                    getSourceReports().parallel().forEach(reportFile -> {
                        String packageName = reportFile.getParentFile().getName();
                        String className = reportFile.getName().replace(".java.html", "");
                        try {
                            final Document doc = Jsoup.parse(reportFile, CHARSET);
                            final Elements lines = doc.select("span[id^=L]");
                            final Elements uncoveredLines = doc.select("span.nc");
                            final Elements coveredLines = doc.select("span.fc");
                            final Elements partiallyCoveredLines = doc.select("span.pc");
                            final ClassCoverage report = new ClassCoverage(packageName, className, coveredLines.size() + partiallyCoveredLines.size(), uncoveredLines.size());
                            boolean inRange = false;
                            int from = 0;
                            ArrayList<Range> ranges = new ArrayList<>();
                            coveredRanges.put(report, ranges);
                            for (Element line : lines) {
                                boolean isCovered = isFullyCovered(line) || isPartiallyCovered(line);
                                if (isCovered && !inRange) {
                                    from = Integer.parseInt(line.id().substring(1));
                                    inRange = true;
                                }
                                if (!isCovered && inRange) {
                                    inRange = false;
                                    int to = Integer.parseInt(line.id().substring(1)) - 1;
                                    ranges.add(Range.closed(from, to));
                                }
                            }
                        } catch (IOException e) {
                            logger.error("Failed to parse file: " + reportFile.getName(), e);
                        } catch (Exception e) {
                            logger.error("Failed abruptly: " + reportFile.getName());
                        }
                    });
                    return coveredRanges;
                } catch (IOException e) {
                    logger.error("Failed to find jacoco html report in dir: " + pathToJacocoReport + " returning empty result", e);
                    return EMPTY_LINES_RESULT;
                }
            }
    );

    private final Supplier<Map<ClassCoverage, Set<MethodCoverage>>> methodsCache = Suppliers.memoize(() -> {
        try {
            Map<ClassCoverage, Set<MethodCoverage>> coveredMethods = new ConcurrentHashMap<>();
            getFileReports().parallel().forEach(reportFile -> {
                String packageName = reportFile.getParentFile().getName();
                String className = reportFile.getName().replace(".html", "");
                try {
                    final Document doc = Jsoup.parse(reportFile, CHARSET);
                    final Elements methods = doc.select("table.coverage > tbody > tr");
                    final Element total = doc.select("table.coverage > tfoot > tr").first();
                    final int linesInClass = Integer.parseInt(total.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
                    final int missingLinesInClass = Integer.parseInt(total.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
                    final ClassCoverage report = new ClassCoverage(packageName, className, linesInClass - missingLinesInClass, missingLinesInClass);
                    Set<MethodCoverage> methodCoverages = new HashSet<>();
                    coveredMethods.put(report, methodCoverages);
                    for (Element method : methods) {
                        int linesInMethod = Integer.parseInt(method.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
                        int missingLinesInMethod = Integer.parseInt(method.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
                        String methodName = method.child(0).child(0).text();
                        if (!methodName.contains("{")) {
                            methodCoverages.add(new MethodCoverage(methodName, linesInMethod - missingLinesInMethod, missingLinesInMethod));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse file: " + reportFile.getName(), e);
                }
            });
            return coveredMethods;
        } catch (IOException e) {
            logger.error("Failed to find jacoco html report in dir: " + pathToJacocoReport + " returning empty result", e);
            return EMPTY_METHODS_RESULT;
        }
    });

    private JacocoReport(Path pathToJacocoReport) {
        this.pathToJacocoReport = pathToJacocoReport;
        File indexFile = pathToJacocoReport.resolve("index.html").toFile();
        try {
            Document doc = Jsoup.parse(indexFile, CHARSET);
            Element titleElement = doc.select("h1").first();
            if (null == titleElement) {
                titleElement = doc.select("h4").first();
            }
            String moduleName = titleElement.text();

            final Element total = doc.select("table.coverage > tfoot > tr").first();
            int linesInModule = Integer.parseInt(total.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
            int missingLinesInModule = Integer.parseInt(total.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
            reportStats = new ReportStats(moduleName, linesInModule, missingLinesInModule);
        } catch (IOException e) {
            logger.error("Failed to find index.html report in dir", e);
            reportStats = new ReportStats("Unknown", 0, 0);
        }
    }

    public static JacocoReport fromHtml(Path pathToHtmlReport) {
        return new JacocoReport(pathToHtmlReport);
    }

    public Map<ClassCoverage, List<Range>> findCoveredRanges() {
        return rangesCache.get();
    }

    public Map<ClassCoverage, Set<MethodCoverage>> findCoveredMethods() {
        return methodsCache.get();
    }

    public ReportStats getReportStats() {
        return reportStats;
    }

    private Stream<File> getSourceReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".java.html"));
    }

    private Stream<File> getFileReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".html")
                && !StringUtils.endsWithAny(f.getName(),
                ".java.html",
                "index.html",
                "index.source.html",
                ".sessions.html",
                "jacoco-sessions.html"));
    }

    private Stream<File> mapToFilesOnly() throws IOException {
        return Files.walk(pathToJacocoReport).map(Path::toFile).filter(f -> !f.isDirectory());
    }

    private boolean isPartiallyCovered(Element line) {
        return line.hasClass("pc");
    }

    private boolean isFullyCovered(Element line) {
        return line.hasClass("fc");
    }
}
