package com.aurea.jacoco;

import com.aurea.jacoco.unit.ClassCoverage;
import com.aurea.jacoco.unit.MethodCoverage;
import com.aurea.jacoco.unit.ModuleCoverage;
import com.aurea.jacoco.unit.PackageCoverage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JacocoReport {

    private static final int NUMBER_OF_LINES_COLUMN = 8;
    private static final int NUMBER_OF_MISSING_LINES_COLUMN = 7;
    private static final String CHARSET = "UTF-8";
    private static final List<String> FILE_REPORT_FILES = Arrays.asList(
            ".java.html",
            "index.html",
            "index.source.html",
            ".sessions.html",
            "jacoco-sessions.html");

    private final Path pathToJacocoReport;
    private final ReportStats reportStats;

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
            throw new JacocoParserException("Failed to find index.html report in " + pathToJacocoReport, e);
        }
    }

    public static JacocoReport fromHtml(Path pathToHtmlReport) {
        return new JacocoReport(pathToHtmlReport);
    }

    public ModuleCoverage getModuleCoverage() {
        try {
            Map<String, List<ClassCoverage>> classCoveragesPerPackage = new HashMap<>();
            getFileReports().parallel().forEach(reportFile -> {
                final String packageName = reportFile.getParentFile().getName();
                final String className = reportFile.getName().replace(".html", "");
                List<ClassCoverage> classCoverages = classCoveragesPerPackage.computeIfAbsent(packageName,
                        (name) -> new ArrayList<>());
                List<MethodCoverage> methodCoverages = new ArrayList<>();

                try {
                    final Document doc = Jsoup.parse(reportFile, CHARSET);
                    final Elements methods = doc.select("table.coverage > tbody > tr");
                    for (Element method : methods) {
                        int linesInMethod = Integer.parseInt(method.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
                        int missingLinesInMethod = Integer.parseInt(method.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
                        String methodName = method.child(0).child(0).text();
                        if (!methodName.contains("{")) {
                            methodCoverages.add(
                                    new MethodCoverage(methodName, 0, 0, linesInMethod - missingLinesInMethod, missingLinesInMethod)
                            );
                        }
                    }
                    classCoverages.add(new ClassCoverage(className, methodCoverages));
                } catch (Exception e) {
                    throw new JacocoParserException("Failed to parse file " + reportFile.getName(), e);
                }
            });
            List<PackageCoverage> packageCoverages = new ArrayList<>();

            classCoveragesPerPackage.entrySet().forEach((entry) -> packageCoverages.add(new PackageCoverage(entry.getKey(), entry.getValue())));
            return new ModuleCoverage(reportStats.getModuleName(), packageCoverages);
        } catch (IOException e) {
            throw new JacocoParserException("Failed to find index.html report in " + pathToJacocoReport, e);
        }
    }

    public ReportStats getReportStats() {
        return reportStats;
    }

    private Stream<File> getSourceReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".java.html"));
    }

    private Stream<File> getFileReports() throws IOException {
        return mapToFilesOnly().filter(f -> f.getName().endsWith(".html")
                && FILE_REPORT_FILES.stream().noneMatch(suffix -> f.getName().endsWith(suffix)));
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
