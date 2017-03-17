package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;
import com.aurea.jacoco.unit.ClassCoverage;
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