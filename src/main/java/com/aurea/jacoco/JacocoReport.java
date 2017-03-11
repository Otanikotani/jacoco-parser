package com.aurea.jacoco;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JacocoReport {

    private static final Logger logger = LogManager.getLogger(JacocoReport.class.getSimpleName());

    private static final int NUMBER_OF_LINES_COLUMN = 8;
    private static final int NUMBER_OF_MISSING_LINES_COLUMN = 7;
    private static final String CHARSET = "UTF-8";

    private Path pathToJacocoReport;
    private ReportStats reportStats;

    private final Supplier<ModuleCoverage> methodsCache = Suppliers.memoize(() -> {
        try {
            ModuleCoverageBuilder moduleBuilder = ModuleCoverageBuilder.aModuleCoverage().withName(reportStats.getModuleName());
            Map<String, PackageCoverageBuilder> packageBuilders = new HashMap<>();
            getFileReports().parallel().forEach(reportFile -> {
                final String packageName = reportFile.getParentFile().getName();
                final String className = reportFile.getName().replace(".html", "");
                PackageCoverageBuilder packageBuilder = packageBuilders.computeIfAbsent(packageName,
                        (name) -> PackageCoverageBuilder.aPackageCoverage().withName(name));
                ClassCoverageBuilder classBuilder = ClassCoverageBuilder.aNewClassCoverage().withName(className);
                try {
                    final Document doc = Jsoup.parse(reportFile, CHARSET);
                    final Elements methods = doc.select("table.coverage > tbody > tr");
                    for (Element method : methods) {
                        int linesInMethod = Integer.parseInt(method.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
                        int missingLinesInMethod = Integer.parseInt(method.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
                        String methodName = method.child(0).child(0).text();
                        if (!methodName.contains("{")) {
                            classBuilder.addMethodCoverage(
                                    new MethodCoverage(methodName, 0, 0, linesInMethod - missingLinesInMethod, missingLinesInMethod)
                            );
                        }
                    }
                    packageBuilder.addClassCoverage(classBuilder.build());
                } catch (Exception e) {
                    logger.error("Failed to parse file: " + reportFile.getName(), e);
                }
            });
            packageBuilders.values().forEach(p -> moduleBuilder.addPackageCoverage(p.build()));
            return moduleBuilder.build();
        } catch (IOException e) {
            logger.error("Failed to find jacoco html report in dir: " + pathToJacocoReport + " returning empty result", e);
            return ModuleCoverage.EMPTY;
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

    public ModuleCoverage getModuleCoverage() {
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
