package com.aurea.jacoco;

import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class JacocoReportTest {

    @Test
    public void testFindCoveredMethods() throws Exception {
        URL jacoco = getClass().getResource("report");
        Path path = Paths.get(jacoco.toURI());
        ModuleCoverage moduleCoverage = JacocoReport.fromHtml(path).getModuleCoverage();


        assertPackages(moduleCoverage.packageCoverages());
        assertClasses(moduleCoverage.classCoverages());
        assertMethods(moduleCoverage.methodCoverages());
    }

    private void assertMethods(Stream<MethodCoverage> methodCoverages) {
        assertThat(methodCoverages.collect(Collectors.toList()))
                .containsOnly(
                        new MethodCoverage("Duplicate(int, Range, int, String)", 0, 0, 0, 6),
                        new MethodCoverage("toString()", 0, 0, 0, 1),
                        new MethodCoverage("distance()", 0, 0, 0, 1),
                        new MethodCoverage("getTimes()", 0, 0, 0, 1),
                        new MethodCoverage("getGroup()", 0, 0, 0, 1),
                        new MethodCoverage("getLines()", 0, 0, 0, 1),
                        new MethodCoverage("getClassName()", 0, 0, 0, 1),
                        new MethodCoverage("equals(Object)", 0, 0, 0, 6),
                        new MethodCoverage("getName()", 0, 0, 0, 1),
                        new MethodCoverage("getTotalLines()", 0, 0, 0, 1),
                        new MethodCoverage("hashCode()", 0, 0, 3, 0),
                        new MethodCoverage("JacocoReport(String, int)", 0, 0, 4, 0),
                        new MethodCoverage("lambda$findCoveredRanges$0(Map, File)", 0, 0, 15, 7),
                        new MethodCoverage("lambda$findCoveredMethods$1(Map, File)", 0, 0, 14, 4),
                        new MethodCoverage("findCoveredRanges()", 0, 0, 3, 3),
                        new MethodCoverage("findCoveredMethods()", 0, 0, 3, 3),
                        new MethodCoverage("lambda$getFileReports$3(File)", 0, 0, 4, 0),
                        new MethodCoverage("JacocoReportParser(Path)", 0, 0, 4, 0),
                        new MethodCoverage("mapToFilesOnly()", 0, 0, 1, 0),
                        new MethodCoverage("lambda$mapToFilesOnly$4(File)", 0, 0, 1, 0),
                        new MethodCoverage("getSourceReports()", 0, 0, 1, 0),
                        new MethodCoverage("getFileReports()", 0, 0, 1, 0),
                        new MethodCoverage("lambda$getSourceReports$2(File)", 0, 0, 1, 0),
                        new MethodCoverage("isPartiallyCovered(Element)", 0, 0, 1, 0),
                        new MethodCoverage("isFullyCovered(Element)", 0, 0, 1, 0),
                        new MethodCoverage("apply(Map.Entry)", 0, 0, 0, 3),
                        new MethodCoverage("main(String[])", 0, 0, 0, 29),
                        new MethodCoverage("lambda$getCoveredLines$6(Multimap, File)", 0, 0, 0, 23),
                        new MethodCoverage("lambda$null$8(Multimap, int, int, Path)", 0, 0, 0, 12),
                        new MethodCoverage("lambda$getDuplicates$9(Multimap, Path)", 0, 0, 0, 8),
                        new MethodCoverage("lambda$main$1(ImmutableListMultimap, Integer)", 0, 0, 0, 5),
                        new MethodCoverage("getCoveredLines(String)", 0, 0, 0, 7),
                        new MethodCoverage("getDuplicates(String)", 0, 0, 0, 5),
                        new MethodCoverage("lambda$main$2(ImmutableListMultimap, Integer)", 0, 0, 0, 4),
                        new MethodCoverage("lambda$printDuplicatesPerClass$4(TreeMap, Integer)", 0, 0, 0, 2),
                        new MethodCoverage("printDuplicatesPerClass(Multimap)", 0, 0, 0, 4),
                        new MethodCoverage("lambda$printDuplicatesPerClass$3(Multimap, TreeMap, String)", 0, 0, 0, 3),
                        new MethodCoverage("lambda$getCoveredLines$5(File)", 0, 0, 0, 1),
                        new MethodCoverage("lambda$main$0(Map, Duplicate)", 0, 0, 0, 1),
                        new MethodCoverage("log(int, int)", 0, 0, 0, 1),
                        new MethodCoverage("lambda$getDuplicates$7(Path)", 0, 0, 0, 1),
                        new MethodCoverage("Main()", 0, 0, 0, 1),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$1(Map, File)", 0, 0, 0, 20),
                        new MethodCoverage("findUncoveredLinesInSettersGetters(String)", 0, 0, 0, 7),
                        new MethodCoverage("isUncovered(Element)", 0, 0, 0, 3),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$0(File)", 0, 0, 0, 1),
                        new MethodCoverage("SettersGettersLocFinder()", 0, 0, 0, 1)
                );
    }

    private void assertClasses(Stream<ClassCoverage> classCoverages) {
        assertThat(classCoverages.collect(Collectors.toList()))
                .usingElementComparator(Comparator.comparing(Named::getName))
                .containsOnly(
                        new ClassCoverage("Duplicate", emptyList()),
                        new ClassCoverage("JacocoReportParser", emptyList()),
                        new ClassCoverage("JacocoReport", emptyList()),
                        new ClassCoverage("Main", emptyList()),
                        new ClassCoverage("Main$1", emptyList()),
                        new ClassCoverage("SettersGettersLocFinder", emptyList())
                );
    }

    private void assertPackages(Stream<PackageCoverage> packageCoverages) {
        assertThat(packageCoverages.collect(Collectors.toList()))
                .usingElementComparator(Comparator.comparing(Named::getName))
                .containsOnly(new PackageCoverage("com.aurea.jacoco", emptyList()));
    }
}