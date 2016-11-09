package com.aurea.jacoco;

import com.google.common.collect.Range;
import one.util.streamex.EntryStream;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JacocoReportTest {

    @Test
    public void testFindCoveredRanges() throws Exception {
        URL jacoco = getClass().getResource("report");
        Path path = Paths.get(jacoco.toURI());

        Map<ClassCoverage, List<Range>> result = JacocoReport.fromHtml(path).findCoveredRanges();

        assertThat(result.get(new ClassCoverage("com.aurea.jacoco.JacocoReportParser", 50, 17))).isNotEmpty();
    }

    @Test
    public void testFindCoveredMethods() throws Exception {
        URL jacoco = getClass().getResource("report");
        Path path = Paths.get(jacoco.toURI());

        Map<ClassCoverage, Set<MethodCoverage>> result = JacocoReport.fromHtml(path).findCoveredMethods();

        assertThat(result.get(new ClassCoverage("com.aurea.jacoco.JacocoReportParser", 50, 17))).isNotEmpty();
    }

    @Test
    public void testActional() throws Exception {
        Map<ClassCoverage, Set<MethodCoverage>> result =
                JacocoReport.fromHtml(Paths.get("C:/crossover/aurea/actional/mybuild/ss/build/debug/common/tests/coverage"))
                        .findCoveredMethods();
        Map<String, Set<MethodCoverage>> namesAsKeys = EntryStream.of(result).mapKeys(ClassCoverage::getName).toMap();
        EntryStream.of(namesAsKeys).forKeyValue((key, value) -> {
            if (key.contains("DBConfigCallback")) {
                System.out.println("Full key: " + key + " values: " + namesAsKeys.get(key));
            }
        });
    }
}