package com.aurea.jacoco.json;

import com.aurea.jacoco.JacocoReport;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class JacocoToJsonConverterTest {

    private JacocoReport jacocoReport;

    @Before
    public void setUp() throws URISyntaxException {
        URL jacoco = getClass().getResource("../report");
        Path path = Paths.get(jacoco.toURI());

        jacocoReport = JacocoReport.fromHtml(path);
    }

    @Test
    public void toJsonSerializesProperly() throws Exception {
        JacocoToJsonConverter converter = JacocoToJsonConverter.of(SerializationFeature.INDENT_OUTPUT);

        String json = converter.toJson(jacocoReport, "test-report-project");

        assertThat(json).contains("\"method\" : \"lambda$findCoveredRanges$0(Map, File)\"");
        assertThat(json).contains("\"name\" : \"test-report-project\"");
    }

    @Test
    public void metaInformationToJsonSerializesProperly() throws Exception {
        JacocoToJsonConverter converter = JacocoToJsonConverter.of(SerializationFeature.INDENT_OUTPUT);

        String json = converter.metaInformationToJson(jacocoReport, "test-report-project");

        assertThat(json).contains("\"total\" : 235");
        assertThat(json).contains("\"name\" : \"test-report-project\"");
    }
}
