package eloc.com.aurea;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
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
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class.getSimpleName());

    public static final Pattern duplicatePattern = Pattern.compile(".+lines (.+) to (.+) in (.+) \\((.+)\\)");

    public static void main(String[] args) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (args.length < 2) {
            System.err.println("Usage: java -jar eloc.jar JACOCO_REPORT_DIR INTELLIJ_DUPLICATES_DIR");
            System.exit(-1);
        }

        final String jacocoDir = args[0];
        final String intellijDuplicatesReportDir = args[1];
        final Multimap<JacocoReport, Range> coveredLines = getCoveredLines(jacocoDir);
        final ImmutableMap<String, JacocoReport> byName = Maps.uniqueIndex(coveredLines.keySet(), JacocoReport::getName);
        final Multimap<String, Duplicate> duplicates = getDuplicates(intellijDuplicatesReportDir);

        Multimap<String, Duplicate> filtered = Multimaps.filterEntries(duplicates, new Predicate<Map.Entry<String, Duplicate>>() {
            @Override
            public boolean apply(Map.Entry<String, Duplicate> entry) {
                Collection<Range> coveredRanges = coveredLines.get(byName.get(entry.getKey()));
                Range<Integer> duplicatedLines = entry.getValue().getLines();
                return coveredRanges.stream().noneMatch(duplicatedLines::isConnected);
            }
        });

        Map<Integer, Integer> uniqueByGroup = new HashMap<>();
        filtered.values().stream()
                .forEach(duplicate -> uniqueByGroup.putIfAbsent(duplicate.getGroup(), duplicate.distance()));

        ImmutableListMultimap<Integer, Duplicate> byGroup = Multimaps.index(duplicates.values(), Duplicate::getGroup);
        int totalNormalizedWin = byGroup.keySet().stream().mapToInt(key -> {
            Collection<Duplicate> duplicatesInGroup = byGroup.get(key);
            int locs = duplicatesInGroup.stream().mapToInt(Duplicate::distance).min().getAsInt();
            int size = duplicatesInGroup.size();
            double complexity = Math.max(log(locs, 2), 1);
            return (int) (locs * (size - 1) / complexity);
        }).sum();

        int totalWin = byGroup.keySet().stream().mapToInt(key -> {
            Collection<Duplicate> duplicatesInGroup = byGroup.get(key);
            int locs = duplicatesInGroup.stream().mapToInt(Duplicate::distance).min().getAsInt();
            int size = duplicatesInGroup.size();
            return locs * (size - 1);
        }).sum();

        System.out.println("Total win: " + totalWin);
        System.out.println("Total normalized win: " + totalNormalizedWin);

        int totalUncoveredDuplicates = uniqueByGroup.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("Total uncovered duplicates: " + totalUncoveredDuplicates);
        Map<String, Integer> setterGetterLines = new SettersGettersLocFinder().findUncoveredLinesInSettersGetters(jacocoDir);
        int totalSetterGetterLines = setterGetterLines.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Total uncovered lines in setters-getters: " + totalSetterGetterLines);

        logger.info("Executed in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
    }

    private static void printDuplicatesPerClass(Multimap<String, Duplicate> filtered) {
        TreeMap<Integer, String> sortedByTotalDuplicatedLines = new TreeMap<>();
        filtered.keySet().forEach(className -> {
            int total = filtered.get(className).stream().mapToInt(Duplicate::distance).sum();
            sortedByTotalDuplicatedLines.put(total, className);
        });
        sortedByTotalDuplicatedLines.descendingKeySet().forEach(key -> {
            System.out.println(String.format("%s: %d", sortedByTotalDuplicatedLines.get(key), key));
        });
    }

    private static Multimap<JacocoReport, Range> getCoveredLines(String jacocoDir) throws IOException {
        Path jacocoRoot = Paths.get(jacocoDir);
        Multimap<JacocoReport, Range> coveredLines = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

        Stream<File> javaCoverageReportsStream = Files.walk(jacocoRoot)
                .map(Path::toFile)
                .filter(f -> !f.isDirectory() && f.getName().endsWith(".java.html"));

        javaCoverageReportsStream.forEach(reportFile -> {
            String name = reportFile.getParentFile().getName() + "." + reportFile.getName().replace(".java.html", "");
            try {
                Document doc = Jsoup.parse(reportFile, "UTF-8");
                Elements lines = doc.select("span[id^=L]");
                int totalLines = lines.size();
                JacocoReport report = new JacocoReport(name, totalLines);
                boolean inRange = false;
                int from = 0;
                for (Element line : lines) {
                    boolean isCovered = line.hasClass("fc") || line.hasClass("pc"); //Consider partially covered lines as covered too
                    if (isCovered && !inRange) {
                        from = Integer.parseInt(line.id().substring(1));
                        inRange = true;
                    }
                    if (!isCovered && inRange) {
                        inRange = false;
                        int to = Integer.parseInt(line.id().substring(1)) - 1;
                        coveredLines.put(report, Range.closed(from, to));
                    }
                }
                if (coveredLines.get(report).isEmpty()) {
                    coveredLines.put(report, Range.closed(0, 0));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return coveredLines;
    }

    private static Multimap<String, Duplicate> getDuplicates(String duplicatesDir) throws IOException {
        Path duplicatesRoot = Paths.get(duplicatesDir);
        Multimap<String, Duplicate> duplicates = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

        Stream<Path> duplicateReportDirs = Files.list(duplicatesRoot).filter(Files::isDirectory);
        duplicateReportDirs.parallel().forEach(p -> {
            try {
                final int group = Integer.parseInt(p.toFile().getName().replace("group", ""));
                List<Path> duplicateReports = Files.list(p).collect(Collectors.toList());
                final int timesDuplicated = duplicateReports.size();
                duplicateReports.stream().forEach(reportPath -> {
                    try {
                        Document doc = Jsoup.parse(reportPath.toFile(), "UTF-8");
                        String header = doc.select("h4").text();
                        Matcher m = duplicatePattern.matcher(header);
                        if (m.find()) {
                            int from = Integer.parseInt(m.group(1).replace(",", ""));
                            int to = Integer.parseInt(m.group(2).replace(",", ""));
                            String className = m.group(4) + "." + m.group(3);
                            duplicates.put(className, new Duplicate(timesDuplicated, Range.closed(from, to), group, className));
                        }
                    } catch (IOException e) {
                        logger.error("Failed to read: " + reportPath);
                    }

                });

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return duplicates;
    }

    private static double log(int x, int base) {
        return Math.log(x) / Math.log(base);
    }
}
