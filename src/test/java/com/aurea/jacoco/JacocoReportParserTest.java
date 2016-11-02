package com.aurea.jacoco;

import com.google.common.collect.Range;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JacocoReportParserTest {

    @Test
    public void testFindCoveredRanges() throws Exception {
        URL jacoco = getClass().getResource("report");
        Path path = Paths.get(jacoco.toURI());

        Map<JacocoReport, List<Range>> result = new JacocoReportParser(path).findCoveredRanges();

        assertThat(result.get(new JacocoReport("com.aurea.jacoco.JacocoReportParser", 67))).isNotEmpty();
    }

    @Test
    public void testFindCoveredMethods() throws Exception {
        URL jacoco = getClass().getResource("report");
        Path path = Paths.get(jacoco.toURI());

        Map<JacocoReport, Set<String>> result = new JacocoReportParser(path).findCoveredMethods();

        assertThat(result.get(new JacocoReport("com.aurea.jacoco.JacocoReportParser", 67))).isNotEmpty();
    }
}