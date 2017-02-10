package com.aurea.jacoco.json;

import com.aurea.jacoco.JacocoReport;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class JacocToJsonConverterTest {

    @Test
    public void toJsonSerializesProperly() throws Exception {
        URL jacoco = getClass().getResource("../report");
        Path path = Paths.get(jacoco.toURI());

        JacocoReport jacocoReport = JacocoReport.fromHtml(path);
        JacocoToJsonConverter converter = JacocoToJsonConverter.of(SerializationFeature.INDENT_OUTPUT);

        String json = converter.toJson(jacocoReport, "test-report-project");

        assertThat(json).contains("\"methodName\" : \"lambda$findCoveredRanges$0(Map, File)\"");
        assertThat(json).contains("\"name\" : \"test-report-project\"");
    }
}
