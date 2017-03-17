package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;
import com.aurea.jacoco.unit.ClassCoverage;
import com.aurea.jacoco.unit.MethodCoverage;
import com.aurea.jacoco.unit.Named;
import com.aurea.jacoco.unit.PackageCoverage;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class XmlReportParserTest extends ParserTest {

    @Test
    public void shouldFindJacocoXmlWhenGivenPathToDirectory() throws Exception {
        Path path = getPathToTestReport();

        JacocoIndex jacocoIndex = JacocoParsers.fromXml(path);

        assertPackages(jacocoIndex.getModuleCoverage().packageCoverages());
        assertClasses(jacocoIndex.getModuleCoverage().classCoverages());
        assertMethods(jacocoIndex.getModuleCoverage().methodCoverages());
    }

    private void assertMethods(Stream<MethodCoverage> methodCoverages) {
        assertThat(methodCoverages.collect(Collectors.toList()))
                .containsExactly(
                        new MethodCoverage("<init>", 0, 15, 0, 6),
                        new MethodCoverage("getTimes", 0, 3, 0, 1),
                        new MethodCoverage("getGroup", 0, 3, 0, 1),
                        new MethodCoverage("getLines", 0, 3, 0, 1),
                        new MethodCoverage("getClassName", 0, 3, 0, 1),
                        new MethodCoverage("distance", 0, 12, 0, 1),
                        new MethodCoverage("toString", 0, 13, 0, 1),
                        new MethodCoverage("<init>", 0, 3, 0, 1),
                        new MethodCoverage("main", 0, 144, 0, 2),
                        new MethodCoverage("printDuplicatesPerClass", 0, 16, 0, 4),
                        new MethodCoverage("getCoveredLines", 0, 23, 0, 7),
                        new MethodCoverage("getDuplicates", 0, 21, 0, 5),
                        new MethodCoverage("log", 0, 8, 0, 1),
                        new MethodCoverage("lambda$getDuplicates$9", 0, 32, 0, 8),
                        new MethodCoverage("lambda$null$8", 0, 76, 0, 2),
                        new MethodCoverage("lambda$getDuplicates$7", 0, 5, 0, 1),
                        new MethodCoverage("lambda$getCoveredLines$6", 0, 118, 0, 16),
                        new MethodCoverage("lambda$getCoveredLines$5", 0, 12, 0, 4),
                        new MethodCoverage("lambda$printDuplicatesPerClass$4", 0, 17, 0, 2),
                        new MethodCoverage("lambda$printDuplicatesPerClass$3", 0, 15, 0, 3),
                        new MethodCoverage("lambda$main$2", 0, 20, 0, 4),
                        new MethodCoverage("lambda$main$1", 0, 30, 0, 5),
                        new MethodCoverage("lambda$main$0", 0, 11, 0, 1),
                        new MethodCoverage("<clinit>", 0, 8, 0, 2),
                        new MethodCoverage("<init>", 0, 3, 0, 1),
                        new MethodCoverage("findUncoveredLinesInSettersGetters", 0, 25, 0, 7),
                        new MethodCoverage("isUncovered", 0, 25, 0, 2),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$1", 0, 99, 0, 14),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$0", 0, 17, 0, 6),
                        new MethodCoverage("<clinit>", 0, 5, 0, 1),
                        new MethodCoverage("<init>", 10, 0, 4, 0),
                        new MethodCoverage("findCoveredRanges", 14, 15, 3, 3),
                        new MethodCoverage("findCoveredMethods", 13, 15, 3, 3),
                        new MethodCoverage("getSourceReports", 5, 0, 1, 0),
                        new MethodCoverage("getFileReports", 5, 0, 1, 0),
                        new MethodCoverage("mapToFilesOnly", 10, 0, 1, 0),
                        new MethodCoverage("isPartiallyCovered", 4, 0, 1, 0),
                        new MethodCoverage("isFullyCovered", 4, 0, 1, 0),
                        new MethodCoverage("lambda$mapToFilesOnly$4", 7, 0, 2, 0),
                        new MethodCoverage("lambda$getFileReports$3", 24, 0, 8, 0),
                        new MethodCoverage("lambda$getSourceReports$2", 5, 0, 1, 0),
                        new MethodCoverage("lambda$findCoveredMethods$1", 77, 22, 3, 1),
                        new MethodCoverage("lambda$findCoveredRanges$0", 74, 43, 7, 7),
                        new MethodCoverage("<clinit>", 9, 0, 3, 0),
                        new MethodCoverage("<init>", 9, 0, 4, 0),
                        new MethodCoverage("getName", 0, 3, 0, 1),
                        new MethodCoverage("getTotalLines", 0, 3, 0, 1),
                        new MethodCoverage("equals", 0, 41, 0, 14),
                        new MethodCoverage("hashCode", 17, 1, 1, 1),
                        new MethodCoverage("<init>", 0, 9, 0, 1),
                        new MethodCoverage("apply", 0, 23, 0, 3)
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