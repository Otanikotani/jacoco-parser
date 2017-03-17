package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;
import com.aurea.jacoco.unit.ClassCoverage;
import com.aurea.jacoco.unit.MethodCoverage;
import com.aurea.jacoco.unit.ModuleCoverage;
import com.aurea.jacoco.unit.Named;
import com.aurea.jacoco.unit.PackageCoverage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aurea.jacoco.parser.XmlReportParser.INVALID_PATH_ERROR;
import static com.aurea.jacoco.parser.XmlReportParser.NULL_PATH_ERROR;
import static com.aurea.jacoco.parser.XmlReportParser.PARSE_ERROR;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class XmlReportParserTest {

    private static final Path JACOCO_EXAMPLES;
    private static final Supplier<IllegalStateException> FAIL = () -> {
        throw new IllegalStateException("Test coverage report is invalid");
    };

    static {
        URL url = XmlReportParserTest.class.getResource("../report");
        try {
            JACOCO_EXAMPLES = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to find ../report folder");
        }
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void failsOnNullPath() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(NULL_PATH_ERROR);

        JacocoParsers.fromXml(null);
    }

    @Test
    public void failsOnInvalidPath() {
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(INVALID_PATH_ERROR, Paths.get("path-to-mars")));

        JacocoParsers.fromXml(Paths.get("path-to-mars"));
    }

    @Test
    public void buildIndexFailsWhenFileIsDeletedAfterParserIsCreated() throws IOException {
        File tempJacocoXml = folder.newFile("jacoco.xml");
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(INVALID_PATH_ERROR, tempJacocoXml.toPath()));

        XmlReportParser parser = new XmlReportParser(tempJacocoXml.toPath());
        boolean delete = tempJacocoXml.delete();
        if (!delete) {
            fail("Failed to delete temp file");
        }

        parser.buildIndex();
    }

    @Test
    public void buildIndexFailsWhenFileIsNotAJacocoReport() throws IOException {
        File tempJacocoXml = folder.newFile("not-a-jacoco-report.xml");
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(PARSE_ERROR, tempJacocoXml.toPath()));

        JacocoParsers.fromXml(tempJacocoXml.toPath());
    }

    @Test
    public void buildIndexFailsWhenFileContainsErrorInMethodElement() {
        Path path = JACOCO_EXAMPLES.resolve("invalid-method-jacoco.xml");
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(PARSE_ERROR, "counter of <init>"));

        JacocoParsers.fromXml(path);
    }

    @Test
    public void buildIndexFailsWhenFileContainsErrorInClassElement() {
        Path path = JACOCO_EXAMPLES.resolve("invalid-class-jacoco.xml");
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(PARSE_ERROR, "class"));

        JacocoParsers.fromXml(path);
    }

    @Test
    public void buildIndexFailsWhenFileContainsErrorInPackageElement() {
        Path path = JACOCO_EXAMPLES.resolve("invalid-package-jacoco.xml");
        expectedException.expect(JacocoParserException.class);
        expectedException.expectMessage(String.format(PARSE_ERROR, "package"));

        JacocoParsers.fromXml(path);
    }

    @Test
    public void shouldFindJacocoXmlWhenGivenPathToDirectory() {
        JacocoIndex jacocoIndex = JacocoParsers.fromXml(JACOCO_EXAMPLES);

        assertPackages(jacocoIndex.getModuleCoverage().packageCoverages());
        assertClasses(jacocoIndex.getModuleCoverage().classCoverages());
        assertMethods(jacocoIndex.getModuleCoverage().methodCoverages());
    }

    @Test
    public void totalOfClassShouldBeEqualToSumOfItsMethods() {
        JacocoIndex jacocoIndex = JacocoParsers.fromXml(JACOCO_EXAMPLES);
        ClassCoverage classCoverage = jacocoIndex.getModuleCoverage().classCoverages().findFirst().orElseThrow(FAIL);
        int sumOfMethods = classCoverage.getMethodCoverages().stream().mapToInt(MethodCoverage::getTotal).sum();

        assertThat(classCoverage.getTotal()).isEqualTo(sumOfMethods);
    }

    @Test
    public void totalOfPackageShouldBeEqualToSumOfItsMethods() {
        JacocoIndex jacocoIndex = JacocoParsers.fromXml(JACOCO_EXAMPLES);
        PackageCoverage packageCoverage = jacocoIndex.getModuleCoverage().packageCoverages().findFirst().orElseThrow(FAIL);
        int sumOfMethods = packageCoverage.getClassCoverages().stream().flatMap(ClassCoverage::methodCoverages)
                .mapToInt(MethodCoverage::getTotal).sum();

        assertThat(packageCoverage.getTotal()).isEqualTo(sumOfMethods);
    }

    @Test
    public void totalOfModuleShouldBeEqualToSumOfItsMethods() {
        JacocoIndex jacocoIndex = JacocoParsers.fromXml(JACOCO_EXAMPLES);
        ModuleCoverage moduleCoverage = jacocoIndex.getModuleCoverage();
        int sumOfMethods = moduleCoverage.getPackageCoverages().stream()
                .flatMap(PackageCoverage::classCoverages)
                .flatMap(ClassCoverage::methodCoverages)
                .mapToInt(MethodCoverage::getTotal)
                .sum();

        assertThat(moduleCoverage.getTotal()).isEqualTo(sumOfMethods);
    }

    private void assertMethods(Stream<MethodCoverage> methodCoverages) {
        assertThat(methodCoverages.collect(Collectors.toList()))
                .containsExactly(
                        new MethodCoverage("Duplicate(int, Range, int, String)", 0, 15, 0, 6),
                        new MethodCoverage("getTimes()", 0, 3, 0, 1),
                        new MethodCoverage("getGroup()", 0, 3, 0, 1),
                        new MethodCoverage("getLines()", 0, 3, 0, 1),
                        new MethodCoverage("getClassName()", 0, 3, 0, 1),
                        new MethodCoverage("distance()", 0, 12, 0, 1),
                        new MethodCoverage("toString()", 0, 13, 0, 1),
                        new MethodCoverage("Main()", 0, 3, 0, 1),
                        new MethodCoverage("main(String[])", 0, 144, 0, 2),
                        new MethodCoverage("printDuplicatesPerClass(Multimap)", 0, 16, 0, 4),
                        new MethodCoverage("getCoveredLines(String)", 0, 23, 0, 7),
                        new MethodCoverage("getDuplicates(String)", 0, 21, 0, 5),
                        new MethodCoverage("log(int, int)", 0, 8, 0, 1),
                        new MethodCoverage("lambda$getDuplicates$9(Multimap, Path)", 0, 32, 0, 8),
                        new MethodCoverage("lambda$null$8(Multimap, int, int, Path)", 0, 76, 0, 2),
                        new MethodCoverage("lambda$getDuplicates$7(Path)", 0, 5, 0, 1),
                        new MethodCoverage("lambda$getCoveredLines$6(Multimap, File)", 0, 118, 0, 16),
                        new MethodCoverage("lambda$getCoveredLines$5(File)", 0, 12, 0, 4),
                        new MethodCoverage("lambda$printDuplicatesPerClass$4(TreeMap, Integer)", 0, 17, 0, 2),
                        new MethodCoverage("lambda$printDuplicatesPerClass$3(Multimap, TreeMap, String)", 0, 15, 0, 3),
                        new MethodCoverage("lambda$main$2(ImmutableListMultimap, Integer)", 0, 20, 0, 4),
                        new MethodCoverage("lambda$main$1(ImmutableListMultimap, Integer)", 0, 30, 0, 5),
                        new MethodCoverage("lambda$main$0(Map, Duplicate)", 0, 11, 0, 1),
                        new MethodCoverage("static {...}", 0, 8, 0, 2),
                        new MethodCoverage("SettersGettersLocFinder()", 0, 3, 0, 1),
                        new MethodCoverage("findUncoveredLinesInSettersGetters(String)", 0, 25, 0, 7),
                        new MethodCoverage("isUncovered(Element)", 0, 25, 0, 2),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$1(Map, File)", 0, 99, 0, 14),
                        new MethodCoverage("lambda$findUncoveredLinesInSettersGetters$0(File)", 0, 17, 0, 6),
                        new MethodCoverage("static {...}", 0, 5, 0, 1),
                        new MethodCoverage("JacocoReportParser(Path)", 10, 0, 4, 0),
                        new MethodCoverage("findCoveredRanges()", 14, 15, 3, 3),
                        new MethodCoverage("findCoveredMethods()", 13, 15, 3, 3),
                        new MethodCoverage("getSourceReports()", 5, 0, 1, 0),
                        new MethodCoverage("getFileReports()", 5, 0, 1, 0),
                        new MethodCoverage("mapToFilesOnly()", 10, 0, 1, 0),
                        new MethodCoverage("isPartiallyCovered(Element)", 4, 0, 1, 0),
                        new MethodCoverage("isFullyCovered(Element)", 4, 0, 1, 0),
                        new MethodCoverage("lambda$mapToFilesOnly$4(File)", 7, 0, 2, 0),
                        new MethodCoverage("lambda$getFileReports$3(File)", 24, 0, 8, 0),
                        new MethodCoverage("lambda$getSourceReports$2(File)", 5, 0, 1, 0),
                        new MethodCoverage("lambda$findCoveredMethods$1(Map, File)", 77, 22, 3, 1),
                        new MethodCoverage("lambda$findCoveredRanges$0(Map, File)", 74, 43, 7, 7),
                        new MethodCoverage("static {...}", 9, 0, 3, 0),
                        new MethodCoverage("JacocoReport(String, int)", 9, 0, 4, 0),
                        new MethodCoverage("getName()", 0, 3, 0, 1),
                        new MethodCoverage("getTotalLines()", 0, 3, 0, 1),
                        new MethodCoverage("equals(Object)", 0, 41, 0, 14),
                        new MethodCoverage("hashCode()", 17, 1, 1, 1),
                        new MethodCoverage("{...}", 0, 9, 0, 1),
                        new MethodCoverage("apply(Map.Entry)", 0, 23, 0, 3)
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