package com.aurea.jacoco;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class JacocoReportParser {

    private static final Logger logger = LogManager.getLogger(JacocoReportParser.class.getSimpleName());

    private static final Map<ClassCoverage, List<Range>> EMPTY_LINES_RESULT = Collections.emptyMap();
    private static final Map<ClassCoverage, Set<MethodCoverage>> EMPTY_METHODS_RESULT = Collections.emptyMap();
    private static final int NUMBER_OF_LINES_COLUMN = 8;
    private static final int NUMBER_OF_MISSING_LINES_COLUMN = 7;

    private final Path pathToJacocoReport;

    public JacocoReportParser(Path pathToJacocoReport) {
        Preconditions.checkNotNull(pathToJacocoReport, "Given path should not be null");
        this.pathToJacocoReport = pathToJacocoReport;
    }

    public Map<ClassCoverage, List<Range>> findCoveredRanges() {
        try {
            Map<ClassCoverage, List<Range>> coveredRanges = new ConcurrentHashMap<>();
            getSourceReports().parallel().forEach(reportFile -> {
                String name = reportFile.getParentFile().getName() + "." + reportFile.getName().replace(".java.html", "");
                try {
                    final Document doc = Jsoup.parse(reportFile, "UTF-8");
                    final Elements lines = doc.select("span[id^=L]");
                    final ClassCoverage report = new ClassCoverage(name, lines.size());
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
                    logger.error("Failed abrupbtly: " + reportFile.getName());
                }
            });
            return coveredRanges;
        } catch (IOException e) {
            logger.error("Failed to find covered ranges in Jacoco report dir: " + pathToJacocoReport, e);
            return EMPTY_LINES_RESULT;
        }
    }

    public Map<ClassCoverage, Set<MethodCoverage>> findCoveredMethods() {
        try {
            Map<ClassCoverage, Set<MethodCoverage>> coveredMethods = new ConcurrentHashMap<>();
            getFileReports().parallel().forEach(reportFile -> {
                String name = reportFile.getParentFile().getName() + "." + reportFile.getName().replace(".html", "");
                try {
                    final Document doc = Jsoup.parse(reportFile, "UTF-8");
                    final Elements methods = doc.select("table.coverage > tbody > tr");
                    final Element total = doc.select("table.coverage > tfoot > tr").first();
                    final ClassCoverage report = new ClassCoverage(name, Integer.parseInt(total.child(NUMBER_OF_LINES_COLUMN).text().replace(",", "")));
                    Set<MethodCoverage> methodCoverages = new HashSet<>();
                    coveredMethods.put(report, methodCoverages);
                    for (Element method : methods) {
                        int linesInMethod = Integer.parseInt(method.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
                        int missingLinesInMethod = Integer.parseInt(method.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
                        Element td = method.firstElementSibling();
                        if (null != td) {
                            Element a = td.firstElementSibling();
                            if (null != a) {
                                String methodName = a.text();
                                if (!methodName.contains("{")) {
                                    methodCoverages.add(new MethodCoverage(methodName, linesInMethod - missingLinesInMethod, missingLinesInMethod));
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to parse file: " + reportFile.getName(), e);
                }
            });
            return coveredMethods;
        } catch (IOException e) {
            logger.error("Failed to find covered methods in Jacoco report dir: " + pathToJacocoReport, e);
            return EMPTY_METHODS_RESULT;
        }
    }

    private Stream<File> getSourceReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".java.html"));
    }

    private Stream<File> getFileReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".html")
                && !f.getName().endsWith("index.html")
                && !f.getName().endsWith("index.source.html")
                && !f.getName().endsWith("jacoco-sessions.html")
                && !f.getName().endsWith(".java.html"));
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
